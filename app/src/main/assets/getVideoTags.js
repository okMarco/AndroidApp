(function() {
  var videoTags = document.getElementsByTagName('video');
  for (var i = 0; i < videoTags.length; i++) {
    var videoTag = videoTags[i];
    var sourceTag = videoTag.childNodes[0];
    var videoUrl = sourceTag.getAttribute('src');
    window.AndroidJS.getVideo(window.location.href, document.title, videoUrl);
  }
})()
