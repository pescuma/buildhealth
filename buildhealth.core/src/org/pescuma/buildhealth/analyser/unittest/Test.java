package org.pescuma.buildhealth.analyser.unittest;

class Test {
	
	final String name;
	final Stats stats = new Stats();
	
	Test(String name) {
		this.name = name;
	}
	
	Stats getStats() {
		return stats;
	}
	
	void visit(TreeVisitor visitor) {
		visitor.visitTest(this);
	}
}
