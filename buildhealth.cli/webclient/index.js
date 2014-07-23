var report = null;

$( document ).ready(function() {

	$.getJSON("report.json")
	.fail(function() {
		$("#build-text").text("Error fetching report");
	})
	.done(function(data) {
		report = data;
		setupPathChange();
		switchTo($.address.pathNames());
	})
	;
});

function setupPathChange() {
	$.address.change(function(event) {
		switchTo(event.pathNames);
	});
}

function findChild(name) {
	for(var i in report.children) {
		var child = report.children[i];
		if (child.name == name)
			return child;
	}
}

function switchTo(paths) {
	var item = decodeURIComponent(paths[0] || "");
	if (item == '') {
		switchToBuild();
		
	} else {
		var child = findChild(item);
		if (child) { 
			switchToInternal(child);
		} else {
			$.address.value('/');
		}
	}
}

function switchToBuild() {
	buildBuild();
	
	$(".item").remove();
	$(".build").show();
}

var buildBuilt = false;

function buildBuild() {
	if (buildBuilt)
		return;
	buildBuilt = true;
	
	var panel = $(".build"); 	
	panel.find(".main-text").text("Your build is " + createTitle(report.status));
	panel.find(".main-img").attr("src", createIcon("build", report.status));
	
	var template = panel.find(".item-template");
	for(var i in report.children) {
		var child = report.children[i];
		var line = template.clone();
		
		line.attr("id", "item-" + i);
		line.attr("class", "item-" + i);
		line.addClass("item-div")
		line.find(".item-text").text(fullText(child));
		line.find(".item-details").text(child.description);
		line.find(".item-img").attr("src", createIcon(getId(child), child.status));
		
		line.click(function(obj) {
			var i = $(this).attr("id").substring(5);
			$.address.value('/' + report.children[i].name);  
		});

		line.show();
		line.appendTo(".build");
	}
}

function switchToInternal(data)
{
	var id = getId(data);
	build(id, data);
	
	$(".item").hide();
	$(".build").hide();
	$("." + id).show();
}

function build(id, data) {
	if ($("." + id).length)
		return;
	
	var config;
	
	if (id == "diskusage") {
		config = [
			{ header: "File", data: "name", size: "1000px" },
			{ header: "Size", data: "value", size: "170px" }
		];
		
	} else if (id == "linesofcode") {
		config = [
			{ header: "Type", data: "name" },
			{ header: "Number of Lines", data: "value" }
		];
		
	} else if (id == "tasks") {
		config = [
			{ header: "Text", data: "name", size:"540px" },
			{ header: "Type", data: "taskType", size:"120px" },
			{ header: "Status", data: "taskStatus", size:"120px" },
			{ header: "Owner", data: "owner", size:"130px" },
			{ header: "Created by", data: "createdBy", size:"130px" },
			{ header: "Creation Date", data: "creationDate", size:"130px" }
		];
		config = removeUnusedColumns(data, config);
		
	} else if (id == "staticanalysis") {
		config = [
			{ header: "Type / File", data: "name", size:"600px" },
			{ header: "Line", data: "value", size:"70px" },
			{ header: "Text", data: "description", size:"500px", wrap:true },
		];
		
	} else if (id == "compilererrors") {
		config = [
			{ header: "File", data: "name", size:"600px" },
			{ header: "Line", data: "value", size:"70px" },
			{ header: "Text", data: "description", size:"500px", wrap:true },
		];
		
	} else if (id == "unittests") {
		config = [
			{ header: "Test", data: "name", size:"600px" },
			{ header: "Result", data: "value", size:"170px" },
			{ header: "Details", data: "description", size:"400px", wrap:true },
		];
		
	} else if (id == "coverage") {
		config = createCoverageColumns(data);
		
	} else {
		config = [
			{ header: "Name", data: "name", size:"450px" },
			{ header: "Value", data: "value", size:"270px" },
			{ header: "Description", data: "description", size:"450px" }
		];
	}
	
	var panel = $(".tree-template").clone();
	panel.attr("class", "container item " + id);
	panel.find(".main-text").text(fullText(data));
	panel.find(".main-img").attr("src", createIcon(id, data.status));
	
	var tree = $('<table></table>').addClass('tree');

	for (var i in config) 
		addCol(tree, config[i].size);
	
	addHeaders(tree, config);
	
	var html = [];
	html.push('<tbody>');
	for(var i in data.children) 
		createLines(html, "", "row-" + i, data.children[i], config);
	html.push('</tbody>');

	tree.append(html.join(''));

	tree.treetable({ expandable: true });	
	panel.append(tree);
	
	$("body").append(panel);
}

function createCoverageColumns(data) {
	var coverage = findFirstByType(data, "CoverageReport");
	if (!coverage)
		return  [
					{ header: "Name", data: "name", size:"450px" },
					{ header: "Value", data: "value", size:"270px" },
					{ header: "Description", data: "description", size:"450px" }
				];
	
	config = [ '' ]; // Reserve space for first element
	var width = 0;
	
	for(var i in coverage.coverages) {
		var name = coverage.coverages[i].name;
		config.push({ 
			header: name, 
			size:"110px",
			type: "percentage",
			value: function(r) {
				var c = findCoverage(r, this.header);
				if (c && c.total > 0)
					return c.percentage;
				else
					return "no data";
			} 
		});
		width += 110;
	}

	if (findCoverage(coverage, "line")) {
		config.push({ 
			header: "Lines", 
			size:"80px",
			value: function(r) {
				var c = findCoverage(r, "line");
				if (c)
					return c.total;
				else
					return "";
			} 
		});
		width += 80;
	}
	
	var left = 1170 - width;
	if (left < 300)
		left = 300;
	
	config[0] = { header: "Name", data: "name", size: left + "px" }
	
	return config;
}

function findCoverage(data, name) {
	for(var i in data.coverages) 
		if (data.coverages[i].name == name) 
			return data.coverages[i];
}

function findFirstByType(data, type) {
	if (data.type == type)
		return data;
	
	for (var child in data.children)
		var result = findFirstByType(child, type);
			if (result)
				return result;
}

function removeUnusedColumns(data, config) {
	var result = [];
	
	for(var i in config)
		if (i == 0 || hasColumn(data, config[i].data))
			result.push(config[i]);
	
	return result;
}

function hasColumn(data, name) {
	if (data[name])
		return true;

	for(var i in data.children)
		if (hasColumn(data.children[i], name))
			return true;
	
	return false;
}

function addHeaders(tree, config) {
	var row = $('<tr></tr>');
	
	for (var i in config) 
		addTh(row, config[i].header);
	
	var thead = $('<thead></thead>');
	thead.append(row);
	tree.append(thead);
}

function createLines(html, parentId, id, data, config) {
	html.push('<tr data-tt-id="' + id + '"');
	if (parentId != '')
		html.push(' data-tt-parent-id="' + parentId + '"');
	if (data['status'] == 'Problematic')
		html.push(' class="problematic-line"');
	else if (data['status'] == 'SoSo')
		html.push(' class="soso-line"');
	html.push('>');
	
	for(var i in config) {
		var cfg = config[i];
		
		var value;
		if (cfg.data)
			value = data[cfg.data];
		else if (cfg.value)
			value = cfg.value(data);
		
		if (value == undefined)
			value = '';
			
		value = htmlEncode(value);
		
		if (cfg.type == "percentage") {
			if (value == "no data")
				value = '';
			else if (value < 50)
				value = '<div class="progressBar"><div class="progressBar-left" style="width: ' + value + '%"></div><div class="progressBar-right">&nbsp;' + value + '%</div></div>';
			else
				value = '<div class="progressBar"><div class="progressBar-left" style="width: ' + value + '%">' + value + '%&nbsp;</div></div>';
		}
		
		addTd(html, !cfg.wrap && i > 0, value, cfg.size);
	}
	
	html.push('</tr>');
	
	for(var i in data.children) 
		createLines(html, id, id + "-" + i, data.children[i], config);
}

function htmlEncode(value) {
	// http://stackoverflow.com/questions/14346414/how-do-you-do-html-encode-using-javascript
	return $('<div/>').text(value).html()
		.replace(/\r?\n/g, '<br>')
		.replace(/\t/g, '&nbsp;&nbsp;&nbsp;&nbsp;')
		.replace(/  +/g, function (x) { return new Array(x.length + 1).join('&nbsp;') });
}

function addTd(html, addDiv, name) {
	if (addDiv) 
		html.push('<td><div>' + name + '</div></td>');
	else
		html.push('<td>' + name + '</td>');
}

function addTh(row, name) {
	var td = $('<th></th>');
	td.text(name);
	row.append(td);
}

function addCol(row, size) {
	if (!size)
		return;

	var col = $('<col width="' + size + '" />');
	row.append(col);
}

function fullText(data) {
	return data.name + (data.value == "" ? "" : ": " + data.value)
}

function createTitle(status) {
	if (status == "Good")
		return "GOOD";
	if (status == "Problematic")
		return "PROBLEMATIC";
	if (status == "SoSo")
		return "So So";
}

function createIcon(type, status) {
	return "icons/" + type + "-" + status.toLowerCase() + ".png";
}

function getId(data) {
	return data.name.toLowerCase().replace(/ /g, "");
}
