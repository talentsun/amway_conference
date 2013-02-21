// JavaScript Document
$("#index").fadeIn();
$(document).ready(function() {
	myScroll.scrollToPage(1, 0, 200);return false
});	

$("#gopictures").bind('click', function() {
    $("#pictures").show();
    $("#pictures").animate({"height": "100%"}, 300);
	myScroll.refresh();
});
$(".goindex").bind('click', function() {
    $("#pictures").animate({"height": "0"}, 300);
    $("#pictures").fadeOut();
});