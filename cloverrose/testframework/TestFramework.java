package cloverrose.testframework;
import java.io.File;
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
	
	//Javaの*.classファイルが置かれているルートディレクトリへの相対パス
	String binDir;
	// テスト用のJavaファイルが定義されているパッケージ
	List<String> packagePaths;
	public void set_binDir_packagePaths(String binDir,List<String> packagePaths){
		this.binDir=binDir;
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
	
	//---------------------------LOGIC PART---------------------------------//
	/**
	 * packagePathsに含まれているクラスを集め、
	 * そのクラスのinitメソッドを呼び出して、
	 * testersを初期化
	 * @throws Exception
	 */
	private void call_init_methods() throws Exception{
		List<String> classNames=new ArrayList<String>();
		for(String packagePath : packagePaths){
			classNames.addAll(getClassNamesInPackage(packagePath));//packagePath直下（非再帰）のクラスの名前をすべて集める
		}
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		for(String className : classNames){
			helper.callMethod(loader, className, methodName);//initメソッドを呼び出す
		}
	}
	/**
	 *  '.'で区切られてるパッケージパスをシステム依存のファイル区切り文字File.separatorに変更
	 *  WindowsではFile.separatorが'\'なので、replaceAllの第２引数にするとエラーが出てしまうので条件分岐している
	 * @param packagePath
	 * @return
	 */
	private String replaceSeparator(String packagePath){
		String sep=File.separator;
		if(sep.equals("\\")){
			return packagePath.replaceAll("\\.",File.separator+File.separator);
		}else{
			return packagePath.replaceAll("\\.",File.separator);
		}
	}
	/**
	 * binDirをルートとしたファイルシステムにおいてpackagePath直下にある
	 * *.classファイルのファイル名（パスを含まない）を集めて返す
	 * その際、拡張子である".class"は削除する
	 * @param packagePath
	 * @return
	 */
	private List<String> getClassFileNames(String packagePath){
		String replaceSeparator=replaceSeparator(packagePath);
		//パッケージの相対パスを作成
		File packageDirectory=new File(binDir+File.separator+replaceSeparator);
		
		//パッケージの下にあるファイルを全て取得
		File[] fs=packageDirectory.listFiles();
		List<String> ret=new ArrayList<String>();
		for(File f : fs){
			String name=f.getName();
			if(name.endsWith(".class")){
				String removeClass=name.replaceAll("\\.class$", "");
				ret.add(removeClass);
			}
		}
		return ret;
	}
	/**
	 * packageNameに含まれるクラス名を集める（packageNameの下のパッケージまでは調べない　非再帰）
	 * cloverrose.sample.testframework.concrete.FiveTesterを生成
	 */
	private List<String> getClassNamesInPackage(String packagePath){
		List<String> ret=new ArrayList<String>();
		for(String name : getClassFileNames(packagePath)){
			String classPath=packagePath+"."+name;
			ret.add(classPath);
		}
		return ret;
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