package cloverrose.testframework;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;
public class TestFramework {
	public TestFramework(){
		this.testers=new ArrayList<Tester>();
	}
	
	//Javaの*.classファイルが置かれているルートディレクトリへの相対パス
	//テスト用のJavaファイルが定義されているパッケージ
	private String binDir;
	private List<String> packagePaths;
	public void set_binDir_packagePaths(String binDir,List<String> packagePaths){
		this.binDir=binDir;
		this.packagePaths=packagePaths;
	}
	//Reflectionを使って呼び出すメソッドの名前[固定]
	private final static String methodName="makeInstance";
	
	//テストを行うインスタンス
	private List<Tester> testers;
	
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
			callMethod(loader, className, methodName);//initメソッドを呼び出す
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
	
	//-----------リフレクション
	private void callMethod(ClassLoader loader,String className,String methodName) 
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException{
		Class<?> clazz = loader.loadClass(className);//クラス名がclassNameのクラスをロード
		Object instance=clazz.newInstance();//クラスからインスタンスを生成
		Method method=clazz.getDeclaredMethod(methodName);//クラスからメソッド名がmethodNameで引数が無いメソッドを取得
		method.setAccessible(true);
		Tester testInstance=(Tester)method.invoke(instance);//instanceのメソッドを引数なしで実行
		this.testers.add(testInstance);
	}
}