package cloverrose.testframework;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class ReflectionHelper {
	void callMethod(ClassLoader loader,String className,String methodName) 
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException{
		Class<?> clazz = loader.loadClass(className);//クラス名がclassNameのクラスをロード
		Object instance=clazz.newInstance();//クラスからインスタンスを生成
		Method method = clazz.getMethod(methodName, new Class[0]);//クラスからメソッド名がmethodNameで引数が無いメソッドを取得
	    method.invoke(instance, new Object[0]);//instanceのメソッドを引数なしで実行
	}
}
