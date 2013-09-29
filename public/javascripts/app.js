/**
 *
 * Created with IntelliJ IDEA.
 * User: pawanacelr
 * Date: 04/09/13
 * Time: 4:44 PM
 * To change this template use File | Settings | File Templates.
 */
var DEBUG = true
var GLOBAL_WIDTH = 0
var GLOBAL_HEIGHT = 0
var HEIGHT_ADJUSTMENT = 0
var MAX_PHONE_TABLET_WIDTH = 1200

function adjustIframes()
{
    if (DEBUG) {console.log("changing iframe geometry..");}
    $('iframe').each(function(){
        var
            $this       = $(this),
            proportion  = $this.data('proportion'),
            w           = $this.attr('width'),
            actual_w    = $this.width();

        if (!proportion)
        {
            proportion = $this.attr('height') / w;
            $this.data('proportion', proportion);
        }

        if (actual_w != w)
        {
            $this.css('height',Math.round(actual_w * proportion) + 'px');
        }
        if (DEBUG) {console.log("changing iframe geometry..");}
    });

    var width =  window.innerWidth || document.body.clientWidth
    var height = window.innerHeight || document.body.clientHeight
    height = height + HEIGHT_ADJUSTMENT
    if (DEBUG) {console.log("New Width:" + width + " and height:" + height)}
    //width = 400
    GLOBAL_WIDTH = width
    GLOBAL_HEIGHT = height
}

$(document).ready(function () {
    if (DEBUG) {console.log("Mobile width related changes")}
    $(".acelr-popup").each(function(){
        $(this).attr("data-placement","top")
    })
    $(window).on('resize load',adjustIframes);
})


/* Fix for IE 10 for properly handing media query issues */
/* Used for storing the image section visited */
var clicked_img_list = new Array()


if (navigator.userAgent.match(/IEMobile\/10\.0/)) {
    var msViewportStyle = document.createElement("style")
    msViewportStyle.appendChild(
        document.createTextNode(
            "@-ms-viewport{width:auto!important}"
        )
    )
    document.getElementsByTagName("head")[0].appendChild(msViewportStyle)
}

