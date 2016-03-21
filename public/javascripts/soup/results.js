$(function(){
    var $body = $('body');

    $body.on('click', '.soup-comment', function(){
        var $context = $(this);
        var entryNumber = $context.data('entry-number');
        var $display = $('.soup-comment-display').filter(function(){
            return $(this).attr('id') === "comment-" + entryNumber;
        });

        var soup = $display.data('entry-name');
        var soupChef = $display.data('entry-chef');
        var soupVotes = $display.data('entry-votes');
        var soupComments = $display.html();

        var $modal = $('#comments-modal');

        $('.soup-name', $modal).html(soupName);
        $('.soup-chef', $modal).html(soupChef);
        $('.soup-votes', $modal).html(soupVotes);
        $('.soup-comments', $modal).html(soupComments);

        $modal.modal();
    });
});
