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

    var nop = function () {
    };

    function width(value) {
        return function () {
            return {width: value};
        }
    }

    function getFontSize(font) {
        var matches = font.match(/([\.\d]+)px/);
        return matches[1];
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
        expect(target(100)).toBe(10);
    });

    it("should translate", function() {
        var target = getCoordinateMapper([1000, 900], 0, 100);
        expect(target(900)).toBe(0);
    });

    it("should scale and translate", function() {
        var target = getCoordinateMapper([1000, 900], 0, 200);
        expect(target(900)).toBe(0);
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

    it("should handle only one domain value", function() {
        var target = getCoordinateMapper([1000], 0, 100);
        expect(target(1000)).toBe(50);
        expect(target(999)).toBeLessThan(50);
    });

    it("should handle empty domain", function() {
        var target = getCoordinateMapper([], 0, 100);
        expect(target(1000)).toBe(1000);
    });

    it("draw wide", function() {
        var time;
        var nCalls = 0;
        var xMin = 10000;
        var xMax = 0;
        var yMin = 10000;
        var yMax = 0;
        var contextMock = {
            fillRect: nop,
            fillText: function (text, x, y) {
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
            },
            measureText: width(200)
        };
        draw(createCanvasMock(contextMock), {"name":"Tilda Andersson","id":"23493","time":"20.05","splits":[
            {"time":"02.40","control":{"x":3386.1,"y":2601.15,"code":"52"}},
            {"time":"10.35","control":{"x":2682.6,"y":2065.8,"code":"55"}}
        ]});
        expect(xMin).not.toBeLessThan(0);
        expect(xMin).toBeLessThan(120);
        expect(xMax).toBeGreaterThan(600);
        expect(xMax).toBeLessThan(800);
        expect(yMin).toBeGreaterThan(10);
        expect(yMin).toBeLessThan(320);
        expect(yMax).toBeGreaterThan(320);
        expect(yMax).toBeLessThan(640);
        expect(nCalls).toBe(2);
        expect(time.charAt(2)).toBe('.');
    });

});
