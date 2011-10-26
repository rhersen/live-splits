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

function draw(canvas, competitor) {
    var context = canvas.getContext('2d');
    var splits = competitor.splits;
    var xs = [];
    var ys = [];
    var i;
    for (i = 0; i < splits.length; ++i) {
        xs.push(splits[i].control.x);
        ys.push(splits[i].control.y);
    }
    var xmapper = getCoordinateMapper(xs, 0, canvas.width - 40);
    var ymapper = getCoordinateMapper(ys, canvas.height - 20, 20);
    for (i = 0; i < splits.length; ++i) {
        context.fillText(splits[i].time, xmapper(splits[i].control.x), ymapper(splits[i].control.y));
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
