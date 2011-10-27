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
function getCanvas() {
    return $('canvas')[0];
}

function getContext() {
    return getCanvas().getContext('2d');
}

function controlRing(context, x, y) {
    context.beginPath();
    context.arc(x, y, 32, 0, 2 * Math.PI, false);
    context.fillStyle = "white";
    context.fill();
    context.stroke();
}
function controlTime(context, text, x, y) {
    context.fillStyle = "black";
    context.fillText(text, x, y);
}
function controlLines(splits, xmapper, ymapper, c) {
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
    return {x:x, y:y, i:i};
}
function draw(canvas, competitor) {
    var c = canvas.getContext('2d');
    var splits = competitor.splits;
    var xs = [];
    var ys = [];
    var i;
    for (i = 0; i < splits.length; ++i) {
        xs.push(splits[i].control.x);
        ys.push(splits[i].control.y);
    }
    var xmapper = getCoordinateMapper(xs, 33, canvas.width - 33);
    var ymapper = getCoordinateMapper(ys, canvas.height - 33, 33);
    var __ret = controlLines(splits, xmapper, ymapper, c);
    var x = __ret.x;
    var y = __ret.y;
    i = __ret.i;
    for (i = 0; i < splits.length; ++i) {
        x = xmapper(splits[i].control.x);
        y = ymapper(splits[i].control.y);
        controlRing(c, x, y);
        controlTime(c, splits[i].time, x, y);
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
