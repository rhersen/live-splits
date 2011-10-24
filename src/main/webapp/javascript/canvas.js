function getCanvas() {
    return $('canvas')[0];
}

function getContext() {
    return getCanvas().getContext('2d');
}

function getMillisSinceUpdate() {
    return getMillisSinceRefresh(state.currentDate, getMillisFromMidnight(state.updated));
}

function draw(context, competitor) {
    context.fillText(competitor.timeString, 120, 120);
}

function init(id) {
    var canvas = getCanvas();
    run();
    window.onresize = handleResize;
    handleResize();

    function run() {
        $.ajax({url: "splits?id=" + id, cache: false, success: handleSuccess, error: handleError});
    }

    function handleSuccess(x) {
        draw(getContext(), x);
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
    }
}
