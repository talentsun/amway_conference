// JavaScript Document
var homescroll,
    myScroll;

function loaded() {
	homescroll = new iScroll('homescroll',{
		onScrollEnd: function () {},
	});
	myScroll = new iScroll('wrapper', {
		snap: true,
		momentum: false,
		hScrollbar: false,
		onScrollEnd: function () {
			document.querySelector('#indicator > li.active').className = '';
			document.querySelector('#indicator > li:nth-child(' + (this.currPageX+1) + ')').className = 'active';
		}
	});
}

document.addEventListener('touchmove', function (e) { e.preventDefault(); }, false);
document.addEventListener('DOMContentLoaded', loaded, false);