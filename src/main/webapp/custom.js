
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

function buildTableModel(data){
    console.log(data);
    var arrOfData = data.split(',');
    arrOfData.splice(0, 1);//remove the first element. Dont know what this is. Ask Elliot
    var arrLength = arrOfData.length;   
    var halfLength = arrLength / 2;
    console.log(halfLength);
    var table = jQuery('<table></table>').addClass('model-answer');
    table.append("<th>Model</th><th>Value</th>"); 
    for(i=0; i < arrLength; i+=2){
        var row = jQuery("<tr><td>" + arrOfData[i] +"</td> <td>" + arrOfData[i+1]  + "</td></tr>");
        table.append(row);
    }
    return table;
}
