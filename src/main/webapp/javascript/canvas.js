var size = 24;

function each(a, f) {
    for (var i = 0; i < a.length; ++i) {
        f(a[i]);
    }
}
function map(a, f) {
    var r = [];

    for (var i = 0; i < a.length; ++i) {
        r.push(f(a[i]));
    }

    return r;
}

function getCanvas() {
    return $('canvas')[0];
}

function getCoordinateMapper(domain, valueForMin, valueForMax) {
    var min, max, scale;

    if (!domain.length) {
        return identity;
    }

    min = Math.min.apply(Math, domain);
    max = Math.max.apply(Math, domain);

    if (min === max) {
        scale = 1;
    } else {
        scale = (valueForMax - valueForMin) / (max - min);
    }

    return coordinateMapper;

    function identity(x) {
        return x;
    }

    function coordinateMapper(x) {
        return valueForMin + scale * (x - min);
    }
}

function draw(canvas, competitor) {
    var c = canvas.getContext('2d');
    var xmapper = getCoordinateMapper(mapControlX(competitor.splits), size + 1, canvas.width - (size + 1));
    var ymapper = getCoordinateMapper(mapControlY(competitor.splits), canvas.height - (size + 1), size + 1);

    drawControlLines(competitor.splits);

    each(competitor.splits, function (split) {
            drawControlSymbol(split.control);
        }
    );

    setFont();

    each(competitor.splits, function (split) {
            var center = mapControl(split.control);
            controlTime(split.time, center.x, center.y);
        }
    );

    function mapControlX(splits) {
        return map(splits, function (split) {
            return split.control.x;
        });
    }

    function mapControlY(splits) {
        return map(splits, function (split) {
            return split.control.y;
        });
    }

    function drawControlSymbol(control) {
        var center = mapControl(control);

        if (control.code.charAt(0) === 'S') {
            drawTriangle(center);
        } else {
            drawControlRing(center.x, center.y, size);
        }

        if (control.code.charAt(0) === 'M') {
            drawControlRing(center.x, center.y, size - 6);
        }

        function drawTriangle(center) {
            c.beginPath();
            c.moveTo(center.x, center.y - size);
            c.lineTo(center.x + size * 0.866, center.y + size / 2);
            c.lineTo(center.x - size * 0.866, center.y + size / 2);
            c.closePath();
            drawPath();
        }

        function drawControlRing(x, y, radius) {
            c.beginPath();
            c.arc(x, y, radius, 0, 2 * Math.PI);
            drawPath();
        }

        function drawPath() {
            c.fillStyle = "white";
            c.fill();
            c.stroke();
        }
    }

    function setFont() {
        c.font = size * 3 / 4 + 'px sans-serif';
        c.textAlign = 'center';
        c.textBaseline = 'middle';
        c.fillStyle = 'black';
    }

    function drawControlLines(splits) {
        c.beginPath();
        c.moveTo(xmapper(splits[0].control.x), ymapper(splits[0].control.y));

        for (var i = 1; i < splits.length; ++i) {
            c.lineTo(xmapper(splits[i].control.x), ymapper(splits[i].control.y));
        }
        
        c.lineWidth = 2;
        c.strokeStyle = "red";
        c.stroke();
    }

    function mapControl(control) {
        return {x:xmapper(control.x), y:ymapper(control.y)};
    }

    function controlTime(time, x, y) {
        c.fillText(time, x, y);
    }
}

function init(id) {
    var competitor;
    run();
    window.onresize = handleResize;
    handleResize();

    function run() {
        $.ajax({url: "splits?id=" + id, cache: false, success: handleSuccess, error: handleError});
    }

    function handleSuccess(c) {
        competitor = c;
        draw(getCanvas(), c);
    }

    function handleError(x) {
        alert(x);
    }

    function handleResize() {
        var margin = 5;
        var canvas = getCanvas();
        canvas.style.marginLeft = margin + "px";
        canvas.style.marginTop = margin + "px";

        canvas.width = window.innerWidth - (margin * 2);
        canvas.height = window.innerHeight - (margin * 2);

        draw(getCanvas(), competitor);
    }
}
