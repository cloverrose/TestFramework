package cloverrose.testframework;
import java.util.List;
import java.util.ArrayList;
public class TestFramework {
	/*------------------------------------------------------------------*/
	/*singleton                                                         */
	/*------------------------------------------------------------------*/
	private static TestFramework singleton=new TestFramework();
	private TestFramework(){
		this.testers=new ArrayList<Tester>();
	}
	public static TestFramework getInstance(){
		return singleton;
	}
    /*------------------------------------------------------------------*/
	
	// テスト用のJavaファイルが定義されているパッケージ
	List<String> packagePaths;
	public void setPackagePaths(List<String> packagePaths){
		this.packagePaths=packagePaths;
	}
	
	//テスト用の出力が標準出力に出ると結果が見づらいのでここにしまう
	private List<String> messages=new ArrayList<String>();
	void addMessage(String msg){
		this.messages.add(msg);
	}
	public List<String> getMessages(){return this.messages;}
	
	//Reflectionを使って呼び出すメソッドの名前
	private final static String methodName="init";
	//Reflection関係のことを行う移譲クラス
	private ReflectionHelper helper=new ReflectionHelper(); 
	
	//テストを行うインスタンス
	private List<Tester> testers;
	void addTester(Tester t){
		this.testers.add(t);
	}
	
	/**
	 * Reflectionを使って、packagePathsに含まれているクラスを集め、
	 * そのクラスのinitメソッドを呼び出して、
	 * testersを初期化
	 * @throws Exception
	 */
	private void call_init_methods() throws Exception{
		List<String> classNames=new ArrayList<String>();
		for(String packagePath : packagePaths){
			classNames.addAll(helper.getClassNamesInPackage(packagePath));//packagePath直下（非再帰）のクラスの名前をすべて集める
		}
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		for(String className : classNames){
			helper.callMethod(loader, className, methodName);//initメソッドを呼び出す
		}
	}
	/**
	 * すべてのテストが真ならtrueを返す
	 * @return
	 */
	public boolean test(){
		try {this.call_init_methods();} catch (Exception e) {e.printStackTrace();}
		boolean flg=true;
		for(Tester t : testers){
			flg&=t.test();
		}
		return flg;
	}
}