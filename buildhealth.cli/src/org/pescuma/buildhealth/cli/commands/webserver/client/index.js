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
		
	} else if (id == "staticanalysis") {
		config = [
			{ header: "Type / File", data: "name", size:"600px" },
			{ header: "Line", data: "value", size:"70px" },
			{ header: "Text", data: "description", size:"500px", wrap:true },
		];
		
	} else if (id == "unittests") {
		config = [
			{ header: "Test", data: "name", size:"600px" },
			{ header: "Result", data: "value", size:"170px" },
			{ header: "Details", data: "description", size:"400px", wrap:true },
		];
		
	} else {
		config = [
			{ header: "Name", data: "name", size:"450px" },
			{ header: "Value", data: "value", size:"270px" },
			{ header: "Description", data: "description", size:"450px" }
		];
	}
	
	config = removeUnusedColumns(data, config);

	var panel = $(".tree-template").clone();
	panel.attr("class", "container item " + id);
	panel.find(".main-text").text(fullText(data));
	panel.find(".main-img").attr("src", createIcon(id, data.status));
	
	var tree = $('<table></table>').addClass('tree');

	for (var i in config) 
		addCol(tree, config[i].size);
	
	addHeaders(tree, config);
	
	var tbody = $('<tbody></tbody>');
	for(var i in data.children) 
		createLines(tbody, "", "row-" + i, data.children[i], config);
	
	tree.append(tbody);

	tree.treetable({ expandable: true });	
	panel.append(tree);
	
	$("body").append(panel);
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

function createLines(tree, parentId, id, data, config) {
	var row = $('<tr></tr>');
	row.attr("data-tt-id", id);
	if (parentId != '')
		row.attr("data-tt-parent-id", parentId);
	
	for(var i in config)
		addTd(row, !config[i].wrap && i > 0, data[config[i].data], config[i].size);	
	
	tree.append(row);
	
	for(var i in data.children) 
		createLines(tree, id, id + "-" + i, data.children[i], config);
}

function addTd(row, addDiv, name) {
	var td = $('<td></td>');
	if (addDiv) {
		var div = $('<div></div>');
		div.text(name);
		td.append(div);
	} else{
		td.text(name);
	}
	row.append(td);
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
	return "/icons/" + type + "-" + status.toLowerCase() + ".png";
}

function getId(data) {
	return data.name.toLowerCase().replace(/ /g, "");
}
