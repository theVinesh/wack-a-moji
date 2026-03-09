//
//  iosAppUITests.swift
//  iosAppUITests
//
//  Created by Vinesh Raju Radhakrishnan on 27/02/2026.
//

import XCTest

final class iosAppUITests: XCTestCase {
    private let rootComposeViewIdentifier = "RootComposeView"

    override func setUpWithError() throws {
        continueAfterFailure = false
    }

    @MainActor
    func testCaptureGameplayScreenshot() throws {
        try captureScreenshot(named: "01_Gameplay", scenario: "gameplay")
    }

    @MainActor
    func testCaptureGameOverScreenshot() throws {
        try captureScreenshot(named: "02_GameOver", scenario: "game-over")
    }

    @MainActor
    private func captureScreenshot(named name: String, scenario: String) throws {
        let app = XCUIApplication()
        setupSnapshot(app)
        app.launchArguments += ["-screenshot-scenario", scenario]
        app.launch()

        XCTAssertTrue(app.otherElements[rootComposeViewIdentifier].waitForExistence(timeout: 5))
        snapshot(name, timeWaitingForIdle: 0)
    }
}
