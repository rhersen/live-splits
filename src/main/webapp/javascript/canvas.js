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
    if (!domain.length) {
        return function (x) {
            return x;
        };
    }
    var max = Math.max.apply(Math, domain);
    var min = Math.min.apply(Math, domain);
    if (min === max) {
        return function (x) {
            return x - min + (valueForMin + valueForMax) / 2;
        };
    }
    var scale = (valueForMax - valueForMin) / (max - min);
    return function (x) {
       return valueForMin + scale * (x - min);
    };
}

function draw(canvas, competitor) {
    var c = canvas.getContext('2d');
    var xmapper = getCoordinateMapper(mapControlX(competitor.splits), 33, canvas.width - 33);
    var ymapper = getCoordinateMapper(mapControlY(competitor.splits), canvas.height - 33, 33);

    controlLines(c, competitor.splits, xmapper, ymapper);

    each(competitor.splits, function (split) {
            var center = mapControl(split.control, xmapper, ymapper);
            controlRing(c, center.x, center.y);
        }
    );

    setFont(c);

    each(competitor.splits, function (split) {
            var center = mapControl(split.control, xmapper, ymapper);
            controlTime(c, split.time, center.x, center.y);
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

    function setFont(c) {
        c.font = '24px sans-serif';
        c.textAlign = 'center';
        c.textBaseline = 'middle';
        c.fillStyle = 'black';
    }

    function controlLines(c, splits, xmapper, ymapper) {
        var i = 1;
        var x = xmapper(splits[0].control.x);
        var y = ymapper(splits[0].control.y);
        c.beginPath();
        c.moveTo(x, y);
        for (i = 1; i < splits.length; ++i) {
            x = xmapper(splits[i].control.x);
            y = ymapper(splits[i].control.y);
            c.lineTo(x, y);
        }
        c.lineWidth = 2;
        c.strokeStyle = "red";
        c.stroke();
    }

    function mapControl(control, xmapper, ymapper) {
        return {x:xmapper(control.x), y:ymapper(control.y)};
    }

    function controlRing(c, x, y) {
        c.beginPath();
        c.arc(x, y, 32, 0, 2 * Math.PI, false);
        c.fillStyle = "white";
        c.fill();
        c.stroke();
    }

    function controlTime(c, time, x, y) {
        c.fillText(time, x, y);
    }
}

function init(id) {
    var canvas = getCanvas();
    var competitor;
    run();
    window.onresize = handleResize;
    handleResize();

    function run() {
        $.ajax({url: "splits?id=" + id, cache: false, success: handleSuccess, error: handleError});
    }

    function handleSuccess(x) {
        competitor = x;
        draw(getCanvas(), x);
    }

    function handleError(x) {
        alert(x);
    }

    function handleResize() {
        var margin = 5;
        canvas.style.marginLeft = margin + "px";
        canvas.style.marginTop = margin + "px";

        canvas.width = window.innerWidth - (margin * 2);
        canvas.height = window.innerHeight - (margin * 2);

        draw(getCanvas(), competitor);
    }
}
