/**
 * handleFile
 *
 * This function gets the file from the input field and creates 
 * the apropriate DOM elements for the UI
 */
function handleFile() {
    var file, fileName, image;
    file = jQuery('#filename')[0].files[0];
    fileName = file.name;
    /*Empty all elements first*/ 
    jQuery('#filemeta').empty();
    /*Create elements*/
    image = document.createElement('img');
    image.src = window.URL.createObjectURL(file);
    jQuery('#filemeta').append("<p>" + "File name: " + fileName + "</p>");
    jQuery('#filemeta').append(image);
}

/**
 * buildTableModel
 *
 * This function build the table for display in the UI
 *
 * @param: csv data
 * 
 * @return: DOM table
 * */

function buildTableModel(data){
    var arrOfData, arrLength, halfLength, table, row;
    arrOfData = data.split(',');
    arrOfData.splice(0, 1);//I know what this is now Elliott :)
    arrLength = arrOfData.length;   
    halfLength = arrLength / 2;
    table = jQuery('<table></table>').addClass('model-answer');
    table.append("<th>Model</th><th>Is this a car?</th>"); 
    for(i=0; i < arrLength; i+=2){
        row = jQuery("<tr><td>" + arrOfData[i] +"</td> <td>" + arrOfData[i+1]  + "</td></tr>");
        table.append(row);
    }
    return table;
}
