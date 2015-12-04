/*
 * Helper code to graph smart city data using nvd3
 * */

	
function generateNVD3(xaxis, yaxis, data, index, title) {

	
	
	/*Muestra datos de salida*/
//	document.getElementById("footer").innerHTML=(data);

	
	nv.addGraph(function() {
		var cumulativeTestData = JSON.parse(data, JSON.dateParser);		
		var firstD = new Date(cumulativeTestData[0].values[0].x);
		
		//funcion para agregar la hora
		
		firstD.setHours(0);
		firstD.setMinutes(0);
		firstD.setSeconds(0);
		
		var lastD = new Date(firstD);
		lastD.setDate(cumulativeTestData[0].values[0].x.getDate()+3);
		lastD.setHours(23);
		lastD.setMinutes(59);
	    lastD.setSeconds(59);
		

	
		
		//modelo de grafica
	    var chart = nv.models.lineChart().margin({
			left : 100
	    }).useInteractiveGuideline(true).showLegend(true).showYAxis(true)
	    .showXAxis(true);

		 //var chart = nv.models.cumulativeLineChart()
		//.useInteractiveGuideline(true)
	    
	    //.x(function(d) { return d[0] }) 
	    //.y(function(d) { return d[1] /100 })
	    //.color(d3.scale.category10().range())
	    
	 
	    
		chart.xAxis.axisLabel(yaxis).tickFormat(function(d) {
			return d3.time.format('%m/%d %H:%M')(new Date(d))
	 
		});
		
			
		chart.yAxis.axisLabel(xaxis).tickFormat(d3.format(',r'));	
		chart.forceX([firstD, lastD]);
		
	
		//d3.select('#chart'+index+' svg').datum(myData).transition().duration(500).call(chart);
		d3.select('#chart'+index+' svg').datum(cumulativeTestData).transition().duration(500).call(chart);
		
		nv.utils.windowResize(function() {
			chart.update()
		});
		
		document.getElementById("chart"+index).style.display = 'block';
		
		document.getElementById("title"+index).innerHTML = title;
		
		return chart;
	});
		
}
function hideNVD3(index) 
{
	document.getElementById("chart"+index).style.display = 'none';
	document.getElementById("title"+index).innerHTML = '';

}

//JSON Date Parser (parseo para la conversion de fechas)
var reISO = /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2}(?:\.\d*))(?:Z|(\+|-)([\d|:]*))?$/;
var reMsAjax = /^\/Date\((d|-|.*)\)[\/|\\]$/;
JSON.dateParser = function(key, value) {
	if (typeof value === 'string') {
		var a = reISO.exec(value);
		if (a)
			return new Date(value);
		a = reMsAjax.exec(value);
		if (a) {
			var b = a[1].split(/[-+,.]/);
			return new Date(b[0] ? +b[0] : 0 - +b[1]);
		}
	}
	return value;
};
	

	