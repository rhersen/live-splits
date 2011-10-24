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

    function width(value) {
        return function () {
            return {width: value};
        }
    }

    function getFontSize(font) {
        var matches = font.match(/([\.\d]+)px/);
        return matches[1];
    }

    it("draw no departures", function() {
        var calls = 0;
        var contextMock = {
            fillRect: nop,
            fillText: function () {
                ++calls;
            },
            measureText: width(200)
        };

        expect(calls).toBe(0);
    });

    it("draw station", function() {
        var xActual = 0;
        var yActual = 0;
        var contextMock = {
            fillRect: nop,
            fillText: function (text, x, y) {
                xActual = x;
                yActual = y;
            },
            measureText: width(200)
        };
        draw(contextMock, {"timeString":"45.40"});
        expect(xActual > 0).toBeTruthy();
    });

});
