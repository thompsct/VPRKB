package vprKBModelTab;

import java.awt.Container;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;

import dataBaseOperations.readDataBase.KBReader;
import dataBaseOperations.readDataBase.ReadLocalFile;
import dataBaseOperations.readDataBase.ReadRDFDatabase;
import semsim.model.physical.PhysicalEntity;
import semsim.model.physical.PhysicalModelComponent;
import semsimKB.SemSimKBConstants;
import semsimKB.SemSimKBConstants.kbcomponentstatus;
import semsimKB.SemSimKBConstants.modelAnnotations;
import semsimKB.annotation.Annotation;
import semsimKB.model.CompBioModel;
import semsimKB.model.ModelLite;
import semsimKB.model.SemSimComponent;
import semsimKB.model.physical.CompositePhysicalEntity;
import semsimKB.model.physical.DBCompositeEntity;
import semsimKB.model.physical.DBPhysicalComponent;
import semsimKB.model.physical.PhysicalProperty;
import vprExplorer.Globals;
import vprExplorer.common.KBListModel;
import vprExplorer.common.KBTableModel;
import vprExplorer.common.fileio;
import vprKBExplorer.Buffer.KBBufferOperations;
import vprKBExplorer.Buffer.StatusPair;
public class VPRKBModelCallBack {

	Globals globals;
	KBBufferOperations KBBuffer;
	ModelLite curmodel = new ModelLite();;
	ModelList ModelCompList;
	KBModelList dbListModel;
	SemSimModelTable ssmodtable;
	KBModelTable kbmodtable;
	modelComposites sscomptable;
	kbComposites kbcomptable;
		
	//Set local knowledge base buffer to be used
	
	public VPRKBModelCallBack(Globals global) {
		globals = global;
		initalize();
	}
	
	//***************************Initializers*********************//
	public void initalize() {
		KBReader rmeth;
		
		if (globals.getService()==Globals.service._FILE) {
			rmeth = new ReadLocalFile();
		}
		else rmeth = new ReadRDFDatabase(globals);
		
		KBBuffer = new KBBufferOperations(rmeth, globals);
	}
	
	public ModelList initSemSimListModel() {
		ModelCompList = new ModelList();
		updateList(ModelCompList);
		return ModelCompList;
	}
	
	public KBModelList initKBListModel() {
		dbListModel = new KBModelList();
		updateList(dbListModel);
		return dbListModel;
	}
	
	//***************************LISTS****************************//
		

	//Update specified list
	protected void updateList(KBListModel list) {
		list.DisplayComponents();
	}
//***************************BUTTONS****************************//
	//Load a user selected model
	public void openButton(Container frame, JLabel log) {
		fileio newfile= new fileio(log);
		if (newfile.chooseNewModel(frame)) {
			loadNewModel(newfile);
		}
	}
	
	//Add Object to buffer
	public int addButton(String selection) {
		PhysicalModelComponent comp = getSelectedSemSimEntry(selection);
		if (comp!=null) {
			int mod = KBBuffer.changeComponent(comp);
			if (mod==1) { 
				return -2; 
			}
			else if (mod==2) {
				int row = dbListModel.indexOf(selection);
				dbListModel.setRowtoBold(row);
				return row;
			}
			
			if (dbListModel.isEmpty()) dbListModel.remove(0);
			Set<String> kbset = new LinkedHashSet<String>();
			
			for (Object[] dbp : dbListModel.getAll()) {
				kbset.add(dbp[0].toString());
			}
			dbListModel.clear();
			kbset.add(selection);
			for (Object s : kbset) {
				dbListModel.addElement(new Object[]{s.toString()});
			}		
			updateList(dbListModel);
		}
		return -1;
	}
	
	//Add all composites
	public void addAllButton() {
		for (int i=0; i <= ModelCompList.getSize()-1; i++) {
			addButton(ModelCompList.getElementAt(i).toString());
		}
	}
	
	//Remove composite from buffer
	public int removefromKBButton(String selection, int index) {
		DBPhysicalComponent comp = (DBPhysicalComponent)getSelectedKBEntry(selection);
		if (KBBuffer.getComponentStatus(comp)==kbcomponentstatus.MISSING) {
			dbListModel.remove(index);
			KBBuffer.removeBufferComponent(comp);
		}
		else {
			dbListModel.removeBold(index);
		}
		updateList(dbListModel);
		return 0;
	}
	
	//**************************SELECTIONS********************************//
	
	public CompositePhysicalEntity getSelectedSemSimEntry(String name) {
		 Set<CompositePhysicalEntity> modComps = curmodel.getCompositePhysicalEntities();
		 for (CompositePhysicalEntity comp : modComps) {
			 if (comp.getName().equals(name)) return comp;
		 }			 
		return null;
	}
	
	private PhysicalModelComponent getSelectedKBEntry(String name) {
		Set<PhysicalModelComponent> dbcomps = (Set<PhysicalModelComponent>) KBBuffer.getComponentSet(SemSimKBConstants.KB_PHYSICAL_CLASS_URI);
		 for (PhysicalModelComponent dbcomp : dbcomps) {
			 if (dbcomp.getName().equals(name)) return dbcomp;
		 }		
	 
		return null;
	}
	
	public DBPhysicalComponent getSelectedDBEntry(String name) {
		 Set<DBPhysicalComponent> modComps = 
				 (Set<DBPhysicalComponent>)KBBuffer.getComponentSet(SemSimKBConstants.KB_PHYSICAL_CLASS_URI);
		 for (DBPhysicalComponent comp : modComps) {
			 if (comp.getName().equals(name)) return comp;
		 }		 
		return null;
	} 
	//Tab specific list classes
	public class ModelList extends KBListModel {
		private static final long serialVersionUID = 1L;

		public void DisplayComponents() {
			if (getSize()==0) {
				DefaultListModel<String> compositelist = new DefaultListModel<String>();
			    curmodel.getCompositeNames(compositelist);
			    for (Object cname : compositelist.toArray()) {
			    	addElement(new Object[]{cname});		    	
			    }
			}
		}
	}
	
	public class KBModelList extends KBListModel {
		private static final long serialVersionUID = 1L;
		
		public void DisplayComponents() {
			clear();
			clearColors();
			clearBold();
			StatusPair sp = new StatusPair();
			for (SemSimComponent comp : KBBuffer.getComponentSet(SemSimKBConstants.KB_PHYSICAL_CLASS_URI)) {
			    addElement(new Object[]{comp.getName()});
			    sp.setPair(getSize(), KBBuffer.getComponentStatus(comp));
			    if (KBBuffer.tobeModified(comp.getURI()) || (KBBuffer.getComponentStatus(comp)==kbcomponentstatus.MISSING)) {
			    	setRowtoBold(getSize()-1);
			    }
			    setRowtoColor(getSize()-1, sp.statusColor());
			}
		}
	}
	
	//****************************CALLBACK_FUNCTIONS*************************
	
	protected void loadNewModel(fileio newfile) {	
		ModelCompList.clear();
		dbListModel.clear();
		curmodel.clear();
		
		newfile.loadSemSimModel(curmodel);
		updateBuffer();	
	}
	//**********************************Initializers**********************
	
		public SemSimModelTable initSemSimModelTable() {
			ssmodtable = new SemSimModelTable();
			ssmodtable.setTitles(new String[] {"Property Name", "Property" });
			ssmodtable.settoDefault();
			return ssmodtable;		
		}
		
		public KBModelTable initKBModelTable() {
			kbmodtable = new KBModelTable();
			kbmodtable.setTitles(new String[] {"Property Name", "Property" });
			kbmodtable.settoDefault();
			return kbmodtable;
		}
				
		public compositeTables initSemSimCompTable() {
			sscomptable = new modelComposites();
			return sscomptable;
		}
		
		public compositeTables initKBComponentTable() {
			kbcomptable = new kbComposites();
			return kbcomptable;
		}
		//Display selection from list
		public void dispSelectedModCompositeInfo(String selection) {
			if (!ModelCompList.isEmpty()) {
					updateSemSimCompTable(selection);
				}
		}
		
		public void dispSelectedKBComponentInfo(String selection) {	
			if (!dbListModel.isEmpty()) {
					updateKBCompTable(selection);
				}
		}
		
		public void updateModelTables() {
			updateSemSimModelTable();
			updateKBModelTable();
		}
			
		protected void updateBuffer() {	
			KBBuffer.CompareModeltoKB(curmodel);

			updateList(ModelCompList);
			updateList(dbListModel);
			
			updateModelTables();
			if (dbListModel.equals(null)) dbListModel.clearList();
		}
		
		//Push to Database button
		public void pushtoDBButton() {
			KBBuffer.pushtoDatabase();
			updateBuffer();	
		}
		
		//****************************TABLE INTERFACES************************
		
		public void updateSemSimModelTable() {
			ssmodtable.updateTable();
		}
		
		public void updateKBModelTable() {
			kbmodtable.updateTable();
		}
		
		public void updateSemSimCompTable(String selection) {
			sscomptable.updateTable(selection);
		}
		
		public void updateKBCompTable(String selection) {
			kbcomptable.updateTable(selection);
		}
		
		public void editKBModTable(int row, String entry) {
	        switch (row) {
	        case 2:
	        	break;
	        case 3:
	        	KBBuffer.addModelEdit(modelAnnotations.MATLAB_MODEL_URL_URI, entry);
	        	break;
	        case 4:
	        	KBBuffer.addModelEdit(modelAnnotations.JSIM_MODEL_URL_URI, entry);
	        	break;
	        case 5:
	        	KBBuffer.addModelEdit(modelAnnotations.CELLML_MODEL_URL_URI, entry);
	        	break;
	        case 6:
	        	break;	           
	        }
		}
		
		//****************************MODEL TABLES****************************	
		abstract class modelTables extends KBTableModel {

			private static final long serialVersionUID = 1L;
			public modelTables(String[] colnames, List<Object[]> table) {
				super(colnames, table);
			}
			public modelTables() {
				super();
			}
			
			public void settoDefault() {
				addRow(new Object[]{"Model Name:", ""});
				fireTableDataChanged();
			}
			abstract void updateTable();

		}
		//Displays information for SemSim formatted model
		public class SemSimModelTable extends modelTables {

			private static final long serialVersionUID = 1L;

			public SemSimModelTable(String[] colnames, List<Object[]> table) {
				super(colnames, table);

			}

			public SemSimModelTable() {
				super();
				addRow(new Object[]{"Model Name:", ""});
				addRow(new Object[]{"URI:", ""});
				addRow(new Object[]{"Description:", ""});
			}

			public void updateTable() {
					clear();
					addRow(new Object[]{"Model Name:", curmodel.getName()});
					addRow(new Object[]{"URI:", curmodel.getURI()});
					addRow(new Object[]{"Description:", curmodel.getDescription()});				
					for (Annotation ann : curmodel.getAnnotations()) 
						addRow(new Object[]{"Annotation:", ann.getValueDescription()});
					
					fireTableDataChanged();
				}
			}
		
		public class KBModelTable extends modelTables {
			boolean editable;
			private static final long serialVersionUID = 1L;

			public KBModelTable(String[] colnames, List<Object[]> table) {
				super(colnames, table);
			}

			public KBModelTable() {
				super();
				addRow(new Object[]{"Model Name:", ""});
				addRow(new Object[]{"URI:", ""});
				addRow(new Object[]{"Author:", ""});
				addRow(new Object[]{"Matlab URL:", ""});
				addRow(new Object[]{"JSim URL:", ""});
				addRow(new Object[]{"CellML URL:", ""});
			}
			
			public void updateTable() {
				CompBioModel cbm = KBBuffer.getKB().getCompBioModelbyName(curmodel.getName());

				setValueAt(cbm.getName().toString(), 0, 1);
				setValueAt(cbm.getURI().toString(), 1, 1);	
				//setValueAt(cbm.getURI().toString(), 2, 1);
				setValueAt(cbm.getModelURL(SemSimKBConstants.MATLAB_MODEL_URL_URI).toString(), 3, 1);	
				setValueAt(cbm.getModelURL(SemSimKBConstants.JSIM_MODEL_URL_URI).toString(), 4, 1);
				setValueAt(cbm.getModelURL(SemSimKBConstants.CELLML_MODEL_URL_URI).toString(), 5, 1);
										
				StatusPair sp = new StatusPair(0, KBBuffer.getComponentStatus(cbm) );
				if (!getValueAt(0, 1).equals("")) {
					setRowtoColor(sp.i, sp.statusColor());
				}
				fireTableDataChanged();
			}
			
			public void setEditFlag(boolean flag) {
				editable=flag;
			}
			
			public boolean isModelEditable(int row, int col) {
					CompBioModel cbm = KBBuffer.getKB().getCompBioModelbyName(curmodel.getName());
						if (KBBuffer.getComponentStatus(cbm).equals(kbcomponentstatus.MISSING)) {				
							if (col==1 && row >= 2 && row < 6) return true;
						} 
				
				return false;
			}
			
		}
		//****************************COMPOSITE TABLES****************************
		abstract class compositeTables extends KBTableModel {

			private static final long serialVersionUID = 1L;
			public compositeTables(String[] colnames, List<Object[]> table) {
				super(colnames, table);
			}

			public compositeTables() {
				super();
			}
			public void settoDefault() {
				addRow(new Object[]{"Component Name:", ""});
				fireTableDataChanged();
			}
									
		}
		public class modelComposites extends compositeTables {

			private static final long serialVersionUID = 1L;

			public modelComposites(String[] colnames, List<Object[]> table) {
				super(colnames, table);
			}

			public modelComposites() {
				super();
			}

			public void updateTable(String selection) {	
				clear();
				CompositePhysicalEntity comp = getSelectedSemSimEntry(selection);	
				if (comp!= null) {
					addRow(new Object[]{"Component Name:", comp.getName()});
					addRow(new Object[]{"URI:", comp.getURI()});
					addRow(new Object[]{"Component Description:", comp.getDescription()});
					
					ArrayList<PhysicalEntity> EntList = comp.getArrayListOfEntities();
					for (PhysicalEntity PEnt : EntList) {
						addRow(new Object[]{"Physical Entity:", PEnt.getName() + " ("
								+ PEnt.getFirstRefersToReferenceOntologyAnnotation().getOntologyAbbreviation()+ ")"});
					}
					Set<PhysicalProperty> PPList = comp.getPhysicalProperties();
					for (PhysicalProperty pp : PPList) {
						addRow(new Object[]{"Associated Properties:", pp.getName()});
					}
					fireTableDataChanged();
				}
			}
		}
		public class kbComposites extends compositeTables {
			
			private static final long serialVersionUID = 1L;

				public kbComposites(String[] colnames, List<Object[]> table) {
				super(colnames, table);
			}

				public kbComposites() {}

				public void updateTable(String selection) {
					clear();

					clearColors();
					Set<StatusPair> statuslist = new HashSet<StatusPair>();
		
					DBCompositeEntity entry = (DBCompositeEntity) getSelectedDBEntry(selection);
					if (entry!=null) {
						
							addRow(new Object[]{"Component Name:", entry.getName()});
							kbcomponentstatus status = KBBuffer.getComponentStatus(entry);
							StatusPair sp = new StatusPair(getRowCount()-1, status );
							statuslist.add(sp);	
							addRow(new Object[]{"URI:", entry.getURI()});
							addRow(new Object[]{"Description:", entry.getDescription()});
							
							if (entry.getClass().equals(DBCompositeEntity.class)) {
								DBCompositeEntity DBPE = ((DBCompositeEntity)entry);
					
								LinkedList<PhysicalModelComponent> EntList =  KBBuffer.getKB().getPhysicalSubComponents(DBPE);
								int i = 0;
								
								for (PhysicalModelComponent PEnt : EntList) {
									if (PEnt.getClassURI()==SemSimKBConstants.REFERENCE_PHYSICAL_ENTITY_CLASS_URI)	
										addRow(new Object[]{"Physical Component:", PEnt.getName()  + " ("
												+ PEnt.getFirstRefersToReferenceOntologyAnnotation().getOntologyAbbreviation()+ ")"});
									else addRow(new Object[]{"Physical Component:", PEnt.getName()});	

									status = KBBuffer.getComponentStatus(PEnt);
									sp = new StatusPair(getRowCount()-1, status );
									statuslist.add(sp);		
									if (i<entry.getRelations().size()) {	
										addRow(new Object[]{"", entry.getRelation(i).getName()});	
										i++;
									}
								}
					
								for (URI modeluri : DBPE.getBioCompModels()) { 
									CompBioModel model = KBBuffer.getKB().getModelbyURI(modeluri);
									addRow(new Object[]{"Model:",model.getName()});
									status = KBBuffer.getComponentStatus(model);
									if (KBBuffer.hasPhysCompStatusList(DBPE)) {
										if (KBBuffer.getPhysComptStatus(DBPE, model.getURI())!=null) 
											status = KBBuffer.getPhysComptStatus(DBPE, model.getURI());
									}
									
									sp = new StatusPair(getRowCount()-1, status );
									if (sp.s==null) System.out.println(model.getName());
									statuslist.add(sp);	
								}
								//Check status of associated properties
								PhysicalProperty pp;
								for (URI ppuri : DBPE.getPropertyList()) {
									pp = KBBuffer.getKB().getPropertybyURI(ppuri);
									if (pp==null) {
										pp = curmodel.getPhysicalPropertybyURI(ppuri);
										status = kbcomponentstatus.MISSING;
									}
									else status = KBBuffer.getComponentStatus(pp);
									addRow(new Object[]{"Physical Property:",pp.getName()});
									
									if (KBBuffer.hasPhysCompStatusList(DBPE)) {
										if (KBBuffer.getPhysComptStatus(DBPE, pp.getURI())!=null) 
											status = KBBuffer.getPhysComptStatus(DBPE, pp.getURI());
									}								
									sp = new StatusPair(getRowCount()-1, status );
									if (sp.s==null) System.out.println(pp.getName());
									statuslist.add(sp);
								}
							}
			
					}
					//Set rows to appropriate colors
					for (StatusPair sp : statuslist) {
						setRowtoColor(sp.i, sp.statusColor());
					}
							
					fireTableDataChanged();
				}
		}
	
	
}
