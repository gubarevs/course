package ua.khai.hubarev.course;

import java.util.Map;
import java.util.Map.Entry;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import ua.khai.hubarev.course.db.Connector;

public class Controller {
	@FXML
	public Label onuName;
	@FXML
	public Label onuFlush;
	@FXML
	public Label length;
	@FXML
	public Label capacity;
	@FXML
	public ToggleGroup radio;
	@FXML
	public TextField txtLine;
	@FXML
	public TextField txtSplitter;
	@FXML
	public void btnCalc_Click(ActionEvent e) {
		RadioButton selected = (RadioButton) radio.getSelectedToggle();
		double line=Double.valueOf(txtLine.getText());
		double splitter=Double.valueOf(txtSplitter.getText());

		Graph g;

		Connector con = new Connector();
		Graph.Arc[] arcs = con.getArcs().toArray(new Graph.Arc[0]);
		Map<Integer,String> ONUs=con.getONUs();
		
		g = Graph.build(arcs.length+1, arcs, 0, 1);
		
		int capasity=0;
		int length=Integer.MAX_VALUE;
		double zat=Integer.MAX_VALUE;
		String name=null;
		
		switch(selected.getText()) {
		case "Минимальное затухание":{
			for(Entry<Integer,String> ent: ONUs.entrySet()) {
				g.setSink(ent.getKey());
				int cap=g.edmondsKarp();
				int localLength=g.getChainLength();
				double localZat=localLength*line+g.getSplittersCount()*splitter;
				if(localZat<zat) {
					capasity=cap;
					name=ent.getValue();
					length=localLength;
					zat=localZat;
				}
			}
		}break;
		case "Минимальное расстояние":{
			for(Entry<Integer,String> ent: ONUs.entrySet()) {
				g.setSink(ent.getKey());
				int cap=g.edmondsKarp();
				int localLength=g.getChainLength();
				if(localLength<length) {
					capasity=cap;
					name=ent.getValue();
					length=localLength;
					zat=localLength*line+g.getSplittersCount()*splitter;
				}
			}
		}break;
		case "Оптимальная разгруженность":{
			for(Entry<Integer,String> ent: ONUs.entrySet()) {
				g.setSink(ent.getKey());
				int cap=g.edmondsKarp();
				if(cap>capasity) {
					capasity=cap;
					name=ent.getValue();
					length=g.getChainLength();
					zat=length*line+g.getSplittersCount()*splitter;
				}
			}
		}break;
		}
		onuName.setText(name);
		onuFlush.setText(Double.toString(zat)+" db");
		capacity.setText(String.valueOf(capasity));
		this.length.setText(String.valueOf(length)+" kм");
	}
}
