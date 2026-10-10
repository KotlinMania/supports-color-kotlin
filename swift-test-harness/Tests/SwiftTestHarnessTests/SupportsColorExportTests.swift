#if canImport(Testing)
import Testing
import SupportsColor

@Suite("SupportsColor Swift Export Tests")
struct SupportsColorExportTests {
    @Test("SupportsColor swift module imported cleanly")
    func testSwiftModuleLoads() throws {
        #expect(Bool(true), "SupportsColor swift module imported cleanly")
    }
}
#elseif canImport(XCTest)
import XCTest
import SupportsColor

final class SupportsColorExportTests: XCTestCase {
    func testSwiftModuleLoads() throws {
        XCTAssertTrue(true, "SupportsColor swift module imported cleanly")
    }
}
#endif
