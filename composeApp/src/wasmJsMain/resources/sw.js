const CACHE_NAME = 'wackamoji-cache-v1';
const FONT_CACHE_NAME = 'wackamoji-font-v1';

// App shell resources to cache immediately on install
const APP_SHELL = [
    './',
    './index.html',
    './composeApp.js',
    './composeApp.wasm',
    './manifest.json',
    './favicon.ico',
    './icon-192.png',
    './icon-512.png',
    './apple-touch-icon.png'
];

self.addEventListener('install', (event) => {
    event.waitUntil(
        caches.open(CACHE_NAME)
            .then((cache) => {
                return cache.addAll(APP_SHELL);
            })
    );
});

self.addEventListener('activate', (event) => {
    event.waitUntil(
        caches.keys().then((cacheNames) => {
            return Promise.all(
                cacheNames.map((cacheName) => {
                    // Keep both the current app cache AND the dedicated font cache
                    if (cacheName !== CACHE_NAME && cacheName !== FONT_CACHE_NAME) {
                        return caches.delete(cacheName);
                    }
                })
            );
        })
    );
    // Claim clients so the service worker takes control immediately
    event.waitUntil(self.clients.claim());
});

self.addEventListener('fetch', (event) => {
    event.respondWith(
        caches.match(event.request)
            .then((response) => {
                // Cache hit - return response
                if (response) {
                    return response;
                }

                // Clone the request because it's a one-time use stream
                const fetchRequest = event.request.clone();

                return fetch(fetchRequest).then(
                    (response) => {
                        // Check if we received a valid response
                        if (!response || response.status !== 200 || response.type !== 'basic') {
                            return response;
                        }

                        // Clone the response because it's a one-time use stream
                        const responseToCache = response.clone();

                        // Determine which cache to use
                        const isFont = event.request.url.includes('NotoColorEmoji');
                        const targetCacheName = isFont ? FONT_CACHE_NAME : CACHE_NAME;

                        caches.open(targetCacheName)
                            .then((cache) => {
                                // Cache any other resources fetched
                                // We only cache GET requests
                                if (event.request.method === 'GET') {
                                    cache.put(event.request, responseToCache);
                                }
                            });

                        return response;
                    }
                ).catch(() => {
                    // Offline fallback
                    console.log('Fetch failed, offline?');
                });
            })
    );
});
