
function handleFile(file) {
    var file = jQuery('#filename')[0].files[0];
    var fileName = file.name;

    /*Empty all elements first*/ 
    jQuery('#filemeta').empty();

    /*Create elements*/
    var image = document.createElement('img');
    image.src = window.URL.createObjectURL(file);
    jQuery('#filemeta').append("<p>" + "File name: " + fileName + "</p>");
    jQuery('#filemeta').append(image);
}
