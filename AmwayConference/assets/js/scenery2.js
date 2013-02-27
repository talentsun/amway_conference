// JavaScript Document
/*$("#index").fadeIn();*/
$("#index").animate({"left": "0"}, 300);
$(document).ready(function() {
	myScroll.scrollToPage(1, 0, 200);return false
});	

$("#gopictures").bind('click', function() {
    $("#pictures").animate({"height": "100%"}, 300);
    $(".pictitle").fadeIn();
    $(".goindex").fadeIn();
	myScroll.refresh();
});
$(".goindex").bind('click', function() {
    $("#pictures").animate({"height": "0"}, 300);
    $(".pictitle").fadeOut();
    $(".goindex").fadeOut();
});