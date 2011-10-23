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

});
