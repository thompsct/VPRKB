package vprExplorer.modeltab;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Set;

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
import semsimKB.model.physical.PhysicalEntity;
import semsimKB.model.physical.PhysicalModelComponent;
import semsimKB.model.physical.PhysicalProperty;
import semsimKB.reading.SemSimOWLreader;
import vprExplorer.Settings;
import vprExplorer.buffer.KBBufferOperations;
import vprExplorer.buffer.StatusPair;
import vprExplorer.common.KBListModel;
import vprExplorer.common.KBTableModel;
import vprExplorer.knowledgebaseinterface.KnowledgeBaseInterface;
import vprExplorer.knowledgebaseinterface.LocalKnowledgeBase;
import vprExplorer.knowledgebaseinterface.RemoteKnowledgeBaseInterface;
public class AddModelWorkbench extends Observable {
	private Settings globals;
	private KnowledgeBaseInterface kboperator;
	private KBBufferOperations kbbuffer;
	
	private ModelLite curmodel;
	
	private ModelList ModelCompList = new ModelList();
	private KBModelList dbListModel= new KBModelList();
	private SemSimModelTable ssmodtable;
	private KBModelTable kbmodtable;
	private modelComposites sscomptable;
	private kbComposites kbcomptable;
		
	//Set local knowledge base buffer to be used
	
	public AddModelWorkbench(Settings global) {
		globals = global;
		initalize();
	}
	
	//***************************Initializers*********************//
	private void initalize() {
		if (globals.getService()==Settings.service._FILE) {
			kboperator = new LocalKnowledgeBase();
		}
		else kboperator = new RemoteKnowledgeBaseInterface(globals);
		
		kbbuffer = new KBBufferOperations(kboperator);
	}
	
	public ModelList initSemSimListModel() {
		return ModelCompList;
	}
	
	public KBModelList initKBListModel() {
		return dbListModel;
	}
	
	//***************************LISTS****************************//
		

	//Update specified list
	protected void updateList(KBListModel list) {
		list.DisplayComponents();
	}
//***************************BUTTONS****************************//
	public String loadSemSimModel(File owlfile) {
		ModelLite semsimmodel;
		SemSimOWLreader ssReader = new SemSimOWLreader();
		try {
			semsimmodel = ssReader.readFromFile(owlfile);
		}
		catch (Exception e) {
			e.printStackTrace();
			return "Failed to load OWL file";
		}
		curmodel = semsimmodel;
		
		ModelCompList.clear();
		dbListModel.clear();
		
		updateBuffer();	
		return "Loaded: " + owlfile.getName();
	}
	
	public void pushtoDatabase() {
		kboperator.pushChangestoDatabase(kbbuffer);
		updateBuffer();	
	}
	
	//Add Object to buffer
	public int addButton(String selection) {
		PhysicalModelComponent comp = getSelectedSemSimEntry(selection);
		if (comp!=null) {
			int mod = kbbuffer.changeComponent(comp);
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
		if (kbbuffer.getComponentStatus(comp)==kbcomponentstatus.MISSING) {
			dbListModel.remove(index);
			kbbuffer.removeBufferComponent(comp);
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
		Set<PhysicalModelComponent> dbcomps = (Set<PhysicalModelComponent>) kbbuffer.getComponentSet(SemSimKBConstants.KB_PHYSICAL_CLASS_URI);
		 for (PhysicalModelComponent dbcomp : dbcomps) {
			 if (dbcomp.getName().equals(name)) return dbcomp;
		 }		
	 
		return null;
	}
	
	public DBPhysicalComponent getSelectedDBEntry(String name) {
		 Set<DBPhysicalComponent> modComps = 
				 (Set<DBPhysicalComponent>)kbbuffer.getComponentSet(SemSimKBConstants.KB_PHYSICAL_CLASS_URI);
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
				ArrayList<String> compositelist = curmodel.getCompositeNames();
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
			for (SemSimComponent comp : kbbuffer.getComponentSet(SemSimKBConstants.KB_PHYSICAL_CLASS_URI)) {
			    addElement(new Object[]{comp.getName()});
			    sp.setPair(getSize(), kbbuffer.getComponentStatus(comp));
			    if (kbbuffer.tobeModified(comp.getURI()) || (kbbuffer.getComponentStatus(comp)==kbcomponentstatus.MISSING)) {
			    	setRowtoBold(getSize()-1);
			    }
			    setRowtoColor(getSize()-1, sp.statusColor());
			}
		}
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
			kbbuffer.CompareModeltoKB(curmodel);

			updateList(ModelCompList);
			updateList(dbListModel);
			
			updateModelTables();
			if (dbListModel.equals(null)) dbListModel.clearList();
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
	        	kbbuffer.addModelEdit(modelAnnotations.MATLAB_MODEL_URL_URI, entry);
	        	break;
	        case 4:
	        	kbbuffer.addModelEdit(modelAnnotations.JSIM_MODEL_URL_URI, entry);
	        	break;
	        case 5:
	        	kbbuffer.addModelEdit(modelAnnotations.CELLML_MODEL_URL_URI, entry);
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
				CompBioModel cbm = kbbuffer.getKB().getCompBioModelbyName(curmodel.getName());

				setValueAt(cbm.getName().toString(), 0, 1);
				setValueAt(cbm.getURI().toString(), 1, 1);	
				//setValueAt(cbm.getURI().toString(), 2, 1);
				setValueAt(cbm.getModelURL(SemSimKBConstants.MATLAB_MODEL_URL_URI).toString(), 3, 1);	
				setValueAt(cbm.getModelURL(SemSimKBConstants.JSIM_MODEL_URL_URI).toString(), 4, 1);
				setValueAt(cbm.getModelURL(SemSimKBConstants.CELLML_MODEL_URL_URI).toString(), 5, 1);
										
				StatusPair sp = new StatusPair(0, kbbuffer.getComponentStatus(cbm) );
				if (!getValueAt(0, 1).equals("")) {
					setRowtoColor(sp.i, sp.statusColor());
				}
				fireTableDataChanged();
			}
			
			public void setEditFlag(boolean flag) {
				editable=flag;
			}
			
			public boolean isModelEditable(int row, int col) {
					CompBioModel cbm = kbbuffer.getKB().getCompBioModelbyName(curmodel.getName());
						if (kbbuffer.getComponentStatus(cbm).equals(kbcomponentstatus.MISSING)) {				
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
							kbcomponentstatus status = kbbuffer.getComponentStatus(entry);
							StatusPair sp = new StatusPair(getRowCount()-1, status );
							statuslist.add(sp);	
							addRow(new Object[]{"URI:", entry.getURI()});
							addRow(new Object[]{"Description:", entry.getDescription()});
							
							if (entry.getClass().equals(DBCompositeEntity.class)) {
								DBCompositeEntity DBPE = ((DBCompositeEntity)entry);
					
								LinkedList<PhysicalModelComponent> EntList =  kbbuffer.getKB().getPhysicalSubComponents(DBPE);
								int i = 0;
								
								for (PhysicalModelComponent PEnt : EntList) {
									if (PEnt.getClassURI()==SemSimKBConstants.REFERENCE_PHYSICAL_ENTITY_CLASS_URI)	
										addRow(new Object[]{"Physical Component:", PEnt.getName()  + " ("
												+ PEnt.getFirstRefersToReferenceOntologyAnnotation().getOntologyAbbreviation()+ ")"});
									else addRow(new Object[]{"Physical Component:", PEnt.getName()});	

									status = kbbuffer.getComponentStatus(PEnt);
									sp = new StatusPair(getRowCount()-1, status );
									statuslist.add(sp);		
									if (i<entry.getRelations().size()) {	
										addRow(new Object[]{"", entry.getRelation(i).getName()});	
										i++;
									}
								}
					
								for (URI modeluri : DBPE.getBioCompModels()) { 
									CompBioModel model = kbbuffer.getKB().getModelbyURI(modeluri);
									addRow(new Object[]{"Model:",model.getName()});
									status = kbbuffer.getComponentStatus(model);
									if (kbbuffer.hasPhysCompStatusList(DBPE)) {
										if (kbbuffer.getPhysComptStatus(DBPE, model.getURI())!=null) 
											status = kbbuffer.getPhysComptStatus(DBPE, model.getURI());
									}
									
									sp = new StatusPair(getRowCount()-1, status );
									if (sp.s==null) System.out.println(model.getName());
									statuslist.add(sp);	
								}
								//Check status of associated properties
								PhysicalProperty pp;
								for (URI ppuri : DBPE.getPropertyList()) {
									pp = kbbuffer.getKB().getPropertybyURI(ppuri);
									if (pp==null) {
										pp = curmodel.getPhysicalPropertybyURI(ppuri);
										status = kbcomponentstatus.MISSING;
									}
									else status = kbbuffer.getComponentStatus(pp);
									addRow(new Object[]{"Physical Property:",pp.getName()});
									
									if (kbbuffer.hasPhysCompStatusList(DBPE)) {
										if (kbbuffer.getPhysComptStatus(DBPE, pp.getURI())!=null) 
											status = kbbuffer.getPhysComptStatus(DBPE, pp.getURI());
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