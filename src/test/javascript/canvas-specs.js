describe('canvas', function() {

    function createCanvasMock(context) {
        return {
            height: 480,
            width: 800,
            getContext: function () {
                return context;
            }
        };
    }

    var nop = function () {};

    function createContextMock() {
        return {
            beginPath: nop,
            closePath: nop,
            moveTo: nop,
            lineTo: nop,
            stroke: nop,
            fill: nop,
            fillText: nop,
            arc: nop
        }
    }

    it("should return identity function", function() {
        var target = getCoordinateMapper([0, 100], 0, 100);
        expect(target(0)).toBe(0);
        expect(target(100)).toBe(100);
    });

    it("should scale", function() {
        var target = getCoordinateMapper([0, 1000], 0, 100);
        expect(target(100)).toBe(10);
    });

    it("should use maximum domain value", function() {
        var target = getCoordinateMapper([0, 10, 1000], 0, 100);
        expect(target(1000)).toBe(100);
    });

    it("should translate", function() {
        var target = getCoordinateMapper([1000, 900], 0, 100);
        expect(target(900)).toBe(0);
    });

    it("should scale and translate", function() {
        var target = getCoordinateMapper([1000, 900], 0, 200);
        expect(target(950)).toBe(100);
    });

    it("should map min to valueForMin", function() {
        var target = getCoordinateMapper([1000, 900], 100, 200);
        expect(target(900)).toBe(100);
    });

    it("should flip if when valueForMin is greater than valueForMax", function() {
        var target = getCoordinateMapper([1000, 900], 200, 100);
        expect(target(900)).toBe(200);
        expect(target(1000)).toBe(100);
    });

    it("should return function with slope 1 if there is only one domain value", function() {
        var target = getCoordinateMapper([1000], 0, 100);
        expect(target(1000)).toBe(0);
        expect(target(1001)).toBe(1);
    });

    it("should return identity function for empty domain", function() {
        var target = getCoordinateMapper([], 0, 100);
        expect(target(1000)).toBe(1000);
    });

    it("should draw", function() {
        var time;
        var nCalls = 0;
        var xMin = 10000;
        var xMax = 0;
        var yMin = 10000;
        var yMax = 0;
        var contextMock = createContextMock();
        contextMock.fillText = function (text, x, y) {
            time = text;
            ++nCalls;
            if (x < xMin) {
                xMin = x;
            }
            if (y < yMin) {
                yMin = y;
            }
            if (x > xMax) {
                xMax = x;
            }
            if (y > yMax) {
                yMax = y;
            }
        };

        draw(createCanvasMock(contextMock), {"name":"Tilda Andersson","id":"23493","time":"20.05","splits":[
            {"time":"02.40","control":{"x":3386.1,"y":2601.15,"code":"52"}},
            {"time":"10.35","control":{"x":2682.6,"y":2065.8,"code":"55"}}
        ],
        "laps":[]});
        expect(xMin).not.toBeLessThan(0);
        expect(xMin).toBeLessThan(120);
        expect(xMax).toBeGreaterThan(600);
        expect(xMax).toBeLessThan(800);
        expect(yMin).toBeGreaterThan(10);
        expect(yMin).toBeLessThan(320);
        expect(yMax).toBeGreaterThan(320);
        expect(yMax).toBeLessThan(640);
        expect(nCalls).toBe(3);
    });

    it("should draw circles", function() {
        var nCircles = 0;
        var contextMock = createContextMock();
        contextMock.arc = function (x, y, radius, startAngle, endAngle) {
            if (endAngle === 2 * Math.PI) {
                ++nCircles;
            }
        };
        draw(createCanvasMock(contextMock), {"name":"Tilda Andersson","id":"23493","time":"20.05","splits":[
            {"time":"00.00","control":{"x":3458.1,"y":2568.9,"code":"S1"}},
            {"time":"12.02","control":{"x":2682.6,"y":2068.8,"code":"56"}},
            {"time":"20.05","control":{"x":3129.6,"y":2671.05,"code":"M1"}}
        ],
        "laps":[]});
        expect(nCircles).toBe(3);
    });

    it("should draw lines", function() {
        var nLines = 0;
        var nTexts = 0;
        var contextMock = createContextMock();
        contextMock.lineTo = function () {
            ++nLines;
        };
        contextMock.fillText = function (text, x, y) {
            ++nTexts;
        };
        draw(createCanvasMock(contextMock), {"name":"Tilda Andersson","id":"23493","time":"20.05","splits":[
            {"time":"00.00","control":{"x":3458.1,"y":2568.9,"code":"S1"}},
            {"time":"12.02","control":{"x":2682.6,"y":2068.8,"code":"56"}},
            {"time":"20.05","control":{"x":3129.6,"y":2671.05,"code":"M1"}}
        ],
        "laps":[]});
        expect(nLines).toBe(4);
        expect(nTexts).toBe(5);
    });

});
