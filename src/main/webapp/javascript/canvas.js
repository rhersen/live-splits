function getCanvas() {
    return $('canvas')[0];
}

function getContext() {
    return getCanvas().getContext('2d');
}

function getMillisSinceUpdate() {
    return getMillisSinceRefresh(state.currentDate, getMillisFromMidnight(state.updated));
}

function init(id, direction) {
    var canvas = getCanvas();
    setInterval(run, 256);
    window.onresize = handleResize;
    handleResize();

    function run() {
    }

    function handleResize() {
        var margin = 5;
        canvas.style.marginLeft = margin + "px";
        canvas.style.marginTop = margin + "px";

        canvas.width = window.innerWidth - (margin * 2);
        canvas.height = window.innerHeight - (margin * 2);
    }
}
