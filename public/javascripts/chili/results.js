$(function(){
    var $body = $('body');

    $body.on('click', '.chili-comment', function(){
        var $context = $(this);
        var entryNumber = $context.data('entry-number');
        var $display = $('.chili-comment-display').filter(function(){
            return $(this).attr('id') === "comment-" + entryNumber;
        });

        var chiliName = $display.data('entry-name');
        var chiliChef = $display.data('entry-chef');
        var chiliVotes = $display.data('entry-votes');
        var chiliComments = $display.html();

        var $modal = $('#comments-modal');

        $('.chili-name', $modal).html(chiliName);
        $('.chili-chef', $modal).html(chiliChef);
        $('.chili-votes', $modal).html(chiliVotes);
        $('.chili-comments', $modal).html(chiliComments);

        $modal.modal();
    });
});