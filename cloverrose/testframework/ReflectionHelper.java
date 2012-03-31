package cloverrose.testframework;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

class ReflectionHelper {
	/**
	 * cloverrose.sample.testframework.concrete と FiveTester.class　から
	 * cloverrose.sample.testframework.concrete.FiveTesterを生成
	 * @param packagePath
	 * @param classPath
	 * @return
	 */
	private String replace(String packagePath,String classPath){
		classPath=classPath.replaceAll("\\.class$", "");
		String ret=packagePath+"."+classPath;
		return ret;
	}
	
	/**
	 * packageNameに含まれるクラス名を集める（packageNameの下のパッケージまでは調べない　非再帰）
	 */
	List<String> getClassNamesInPackage(String packagePath) throws IOException{
		List<String> ret=new ArrayList<String>();
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		JavaFileManager fm = compiler.getStandardFileManager(
		    new DiagnosticCollector<JavaFileObject>(), null, null);
	
		// 一覧に含めるオブジェクト種別。以下はクラスのみを含める。
		Set<JavaFileObject.Kind> kind = new HashSet<JavaFileObject.Kind>(){
			private static final long serialVersionUID = 1L;
			{
			    add(JavaFileObject.Kind.CLASS);
			}
		};
		
		for (JavaFileObject f : fm.list(StandardLocation.CLASS_PATH, packagePath, kind, false)) {
			URI uri=f.toUri();
			File file=new File(uri);
			ret.add(this.replace(packagePath,file.getName()));
		}		
		return ret;		
	}
	void callMethod(ClassLoader loader,String className,String methodName) 
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException{
		Class<?> clazz = loader.loadClass(className);//クラス名がclassNameのクラスをロード
		Object instance=clazz.newInstance();//クラスからインスタンスを生成
		Method method = clazz.getMethod(methodName, new Class[0]);//クラスからメソッド名がmethodNameで引数が無いメソッドを取得
	    method.invoke(instance, new Object[0]);//instanceのメソッドを引数なしで実行
	}
}
