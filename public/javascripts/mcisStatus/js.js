(function() {
    var date = new Date($('#last-refreshed').data('time'));
    $('#last-refreshed').html(date.toTimeString());

    $('.show-tooltip').tooltip({ container: 'body' });

    $('.refresh-link').click(function(){
        location.reload();
    });

    var isPaused = false;
    $('.pause-link').click(function(){
        isPaused = true;
        $('.running, .paused').toggle(); 
    });

    $('.start-link').click(function(){
        isPaused = false;
        $('.running, .paused').toggle();

    });

    var refreshInterval = setInterval(refreshPage, 1000);

    function refreshPage() {
        if(isPaused) {
            return;
        } else {
            var timeLeft = parseInt($('.refresh-time').html());
            if(timeLeft === 0) {
                clearInterval(refreshInterval);
                location.reload();
            } else {
                timeLeft -= 1;
                $('.refresh-time').html(timeLeft);
            }
        }
    }
})()
