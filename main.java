import org.apache.commons.csv.* ;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.output.* ;
import org.apache.commons.codec.binary.* ;
import java.util.List;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Map;
import javax.script.SimpleBindings;
public class main { //TODO: Il faut faire Livraison, Stock et Facture .csv, et finir ce truc.
	public static void main(String[] args){
		Model testModel = new Model("./client.csv","./commande.csv","./livraison.csv","./facture.csv");
		UnitTestManager UnitTest = new UnitTestManager(testModel);
		UnitTest.BatterieUT();
	}
}

class GlobalDump{
	//public static final int SWITCHSTOCK = 1; //Inutilisé
	public static final int SWITCHCLIENT = 2;
	public static final int SWITCHCOMMANDE = 3;
	public static final int SWITCHLIVRAISON = 4;
	public static final int SWITCHFACTURE = 5;


	public static final int ENTRYID = 0;

	public static final int COMMANDECLIENT = 1;
	public static final int COMMANDELIVRAISON = 2;
	public static final int COMMANDEFACTURE = 3;

	public static final int LIVRAISONSTATUS = 3;

	public static final int FACTURESTATUS = 2;

	public static final int PROGRESSNOCOMMANDE = 1;
	public static final int PROGRESSNOTSHIPPEDABSENT = 2;
	public static final int PROGRESSNOTSHIPPEDFALSE = 3;
	public static final int PROGRESSNOTPAIDABSENT = 4;
	public static final int PROGRESSNOTPAIDFALSE = 5;
	public static final int PROGRESSDONE = 6;


	
}

class UnitTestManager{
	private final Model testModel;
	private Controller testController ;
	private View testView ;
	private InterfaceViewController testInterface ;


	public UnitTestManager(Model model){
		testModel = model ;
		testController = new Controller(testModel);
		testView = new View();
		testInterface = new InterfaceViewController(testView, testController);
	}

	public void BatterieUT(){
		System.out.println("Hello World!");
		//setUpTestEnv();
		testController.GetAllClients();
		testModel.TUReturnSingleEntry();
		testView.printEntryList(testModel.ClientCommandeList("42494"));
		UTClientCommande();
	}


	

	private void UTClientCommande(){
		testView.printClientCommande(testInterface.requestClientCommandes("42494")) ;
		System.out.println("=========================");
		testView.printClientCommande(testInterface.requestClientCommandes("38324")) ;
		System.out.println("=========================");
		testView.printClientCommande(testInterface.requestClientCommandes("64724")) ;

	}

}
class InterfaceViewController{
	private View view;
	private Controller controller;
	InterfaceViewController(View addedView, Controller addedController){
		view = addedView;
		view.SetViewInterface(this);
		controller = addedController;
		controller.SetControllerInterface(this);
	}

	public void TransmitDBAndType(List<String[]> data, int type){
		view.DisplayWholeDB(data, type); 
	}

	public void requestClientDB(){
		controller.GetAllClients();
	}

	public SimpleBindings requestClientCommandes(String clientID){
		return controller.GetCommandeStatusOflient(clientID);
	}
}

class View{ //TODO: View devrait devenir abstraite pour être implémenté par des classes filles pour des types d'interfaces différentes.

	private InterfaceViewController viewedInterface = null ;

	public void SetViewInterface(InterfaceViewController target){
		viewedInterface = target;
	}

	public void DisplayWholeDB(List<String[]> data, int dataSwitch){
		switch(dataSwitch){
			case GlobalDump.SWITCHCLIENT:
				System.out.println("ID du Client | Nom du Client | Téléphone du Client | Adresse du client |");
				break;
			
		}

		printEntryList(data);
	}

	public void printEntryList(List<String[]> data){
		int a = 0 ; //index pour les itérateurs
		int b = 0 ;
		for(String[] entry : data){
			for(String values : entry){
				System.out.print(data.get(a)[b] + " | ");
				b++ ;
			}
			b = 0 ; //Il faut reset avant la prochaine itération.
			System.out.print("\n");
			a++;
		}
	}

	public void printClientCommande(SimpleBindings data){
		
		data.forEach((k,v)->{System.out.print("Commande N°" + k + " : " + switchProgress(v.toString()) + "\n");});
	}

	public String switchProgress(String arg){
		int argint = new Integer(arg) ; // Object -> String -> int. J'avais pas envie d'essayer de caster l'Object en int directement.
		switch(argint){
			case GlobalDump.PROGRESSNOTSHIPPEDABSENT :
				return "Commande en cours de livraison, il n'y a pas d'information de livraison." ;
			case GlobalDump.PROGRESSNOTSHIPPEDFALSE :
				return "Commande en cours de livraison, la commande n'a pas encore été livrée." ;
			case GlobalDump.PROGRESSNOTPAIDABSENT :
				return "Commande en cours de facturation, il n'y a pas d'information de facturation." ;
			case GlobalDump.PROGRESSNOTPAIDFALSE :
				return "Commande en cours de facturation, la facture n'a pas encore été validée." ;
			case GlobalDump.PROGRESSDONE :
				return "Commande clôturée avec succès.";
		}
		return "ERREUR : Pas de commande détectée !";
		
	}

} 

class Controller{
	private final Model controlledModel;
	private InterfaceViewController controlledInterface = null ;
	public Controller(Model model){
		controlledModel = model ;

	}
	public void SetControllerInterface(InterfaceViewController target){
		controlledInterface = target;
	}
	public void GetAllClients(){
		controlledInterface.TransmitDBAndType(controlledModel.ReturnWholeDB(GlobalDump.SWITCHCLIENT), GlobalDump.SWITCHCLIENT);
	}
	/**public void GetAllStocks(){
		controlledInterface.TransmitDBAndType(controlledModel.ReturnWholeDB(GlobalDump.SWITCHSTOCK), GlobalDump.SWITCHSTOCK);
	}**/
	public void GetAllCommandes(){
		controlledInterface.TransmitDBAndType(controlledModel.ReturnWholeDB(GlobalDump.SWITCHCOMMANDE), GlobalDump.SWITCHCOMMANDE);
	}
	public void GetAllLivraisons(){
		controlledInterface.TransmitDBAndType(controlledModel.ReturnWholeDB(GlobalDump.SWITCHLIVRAISON), GlobalDump.SWITCHLIVRAISON);
	}
	public void GetAllFactures(){
		controlledInterface.TransmitDBAndType(controlledModel.ReturnWholeDB(GlobalDump.SWITCHFACTURE), GlobalDump.SWITCHFACTURE);
	}

	public SimpleBindings GetCommandeStatusOflient(String clientID){
		SimpleBindings returnMap = new SimpleBindings() ;
		List<String[]> commandes = controlledModel.ClientCommandeList(clientID);
		for(String[] entry : commandes){
			returnMap.put(entry[GlobalDump.ENTRYID], controlledModel.CheckCommandeProgress(entry[GlobalDump.ENTRYID]));
		}
		return returnMap;
	}
	 
}


class Model{
	private final ModelTemplate ModelClientele ;
	private final ModelTemplate ModelCommande ;
	private final ModelTemplate ModelLivraison ;
	private final ModelTemplate ModelFacture ;
	
	public Model(String pathClient, String pathCommande, String pathLivraison, String pathFacture){
		ModelClientele = new ModelTemplate(pathClient);
		ModelCommande = new ModelTemplate(pathCommande);
		ModelLivraison = new ModelTemplate(pathLivraison);
		ModelFacture = new ModelTemplate(pathFacture);
	}
	
	public List<String[]> ReturnWholeDB(int type){
		switch(type){
			case GlobalDump.SWITCHCLIENT: 
				return ModelClientele.ReturnWholeDB();
			case GlobalDump.SWITCHCOMMANDE:
				return ModelCommande.ReturnWholeDB();
			case GlobalDump.SWITCHLIVRAISON:
				return ModelLivraison.ReturnWholeDB();
			case GlobalDump.SWITCHFACTURE:
				return ModelFacture.ReturnWholeDB();
		}
		System.out.println("ERROR : The overarching model tried to access a database that does not exist.");
		return null;
	}

	public List<String[]> ClientCommandeList(String clientID){
		List<String[]> returnList = new ArrayList<>();
		for(String[] entry : ModelCommande.ReturnWholeDB()){
			if(clientID.compareTo(entry[GlobalDump.COMMANDECLIENT]) == 0){
				returnList.add(entry);
			}
		}
		if(returnList.size() == 0){
			System.out.println("No matches");
		}
		return returnList ;
		
	}

	public void TUReturnSingleEntry(){
		System.out.println(ModelClientele.ReturnSingleEntry("42494"));
	}

	public int CheckCommandeProgress(String id){
		int returnValue = 0;
		String booleanBuffer = null;
		int a = 0 ; //Sert à la compairaison des identifiants.
		int b = 0 ;
		String[] commande = ModelCommande.ReturnSingleEntry(id) ;
		if(commande == null){
			return GlobalDump.PROGRESSNOCOMMANDE ;
		}
		List<String[]> DBLivraison = ModelLivraison.ReturnWholeDB();
		for(String[] entryLivraison : DBLivraison){
			a = Integer.parseUnsignedInt(entryLivraison[GlobalDump.ENTRYID]);
			b = Integer.parseUnsignedInt(commande[GlobalDump.COMMANDELIVRAISON]) ;
			if(a == b){
				returnValue = GlobalDump.PROGRESSNOTSHIPPEDFALSE; //Si la livraison a eu lieu, une hypothétique valeur correcte serait écrasé de toute manière dû aux tests suivants.
				booleanBuffer = entryLivraison[GlobalDump.LIVRAISONSTATUS] ;
				break ;
			}
		}
		if(returnValue != GlobalDump.PROGRESSNOTSHIPPEDFALSE){
			return GlobalDump.PROGRESSNOTSHIPPEDABSENT;
		}
		if(Boolean.getBoolean(booleanBuffer)){
			return returnValue;
		}

		List<String[]> DBFacture = ModelFacture.ReturnWholeDB();
		for(String[] entryFacture : DBFacture){
			a = Integer.parseUnsignedInt(commande[GlobalDump.COMMANDEFACTURE]);
			b = Integer.parseUnsignedInt(entryFacture[GlobalDump.ENTRYID]) ;
			if(a == b){
				returnValue = GlobalDump.PROGRESSNOTPAIDFALSE; 
				booleanBuffer = entryFacture[GlobalDump.FACTURESTATUS] ;
				break ;
			}
		}

		if(returnValue != GlobalDump.PROGRESSNOTPAIDFALSE){
			return GlobalDump.PROGRESSNOTPAIDABSENT ;
		}

		if(Boolean.getBoolean(booleanBuffer)){
			return returnValue;
		}

		return GlobalDump.PROGRESSDONE ;

	}

}

class ModelTemplate{ //TODO:Créer un dictionnaire liant la clé primaire contenu dans un registre avec l'index du dit registre dans la liste serait une bonne idée.
	private List<String[]> recordList = new ArrayList<>();//Les valeurs d'un registre CSV sont (entre autres) acquises par la fonctions values() qui retourne un tableau de Strings doù le type d'élement de liste.
	public ModelTemplate(String path){
		File csvData = new File(path);
		try{CSVParser parser = CSVParser.parse(csvData, null, CSVFormat.RFC4180);
			for (CSVRecord csvRecord : parser){
				recordList.add((int)csvRecord.getRecordNumber()-1, csvRecord.values()) ;
			}
			System.out.println(this);
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	/**private void WriteToCSV(String path){}
	private bool DeleteFromCSV(int id){}**/
	public String[] ReturnSingleEntry(String id){
		for(String[] entry : recordList){ // Idéalement, on comparerai à un dictionnaire id:entrée calculé séparément, mais bon.
			if(id.compareTo(entry[GlobalDump.ENTRYID]) == 0){
				return entry;
			}
		}
		System.out.println("No entry found");
		return null;
	}
	public List<String[]> ReturnWholeDB(){
		return recordList ;
	}
}
