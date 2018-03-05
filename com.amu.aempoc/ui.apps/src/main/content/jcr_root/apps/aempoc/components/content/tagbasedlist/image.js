"use strict";

/**
 * Image component JS Use-api script
 */
use(["/libs/wcm/foundation/components/utils/Image.js",
     "/libs/sightly/js/3rd-party/q.js"], function (Image, Q) {

    var imageResource = resolver.resolve(this.pagePath + '/jcr:content/image');

    var image = new Image(imageResource);
    var imageDefer = Q.defer();

    // check if there's a local file image under the node
    granite.resource.resolve(this.pagePath + "/jcr:content/image/file").then(function (localImageResource) {
        imageDefer.resolve(image);
    }, function () {
        imageDefer.resolve(image);
    });
    return imageDefer.promise;
});
