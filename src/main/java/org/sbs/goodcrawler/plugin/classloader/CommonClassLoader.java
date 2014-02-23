package org.sbs.goodcrawler.plugin.classloader;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * @author shenbaise（shenbaise1001@126.com）
 * @desc 从指定目录、jar文件加载类<br>
 *       使用：通过下面的方式扩展当前加载器</br> CommonClassLoader cld = new
 *       CommonClassLoader(Thread.currentThread().getContextClassLoader());</br>
 *       cld.addEtries(****);</br>
 *       Thread.currentThread().setContextClassLoader();</br>
 */
@SuppressWarnings("rawtypes")
public class CommonClassLoader extends ClassLoader {

	// 类资源,目录或者是jar
	private List<File> entries = new ArrayList<File>();

	/**
	 * @param parent
	 * @param entries
	 */
	public CommonClassLoader(ClassLoader parent, File[] entries) {
		super(parent);
		this.entries.addAll(Arrays.asList(entries));
	}

	/**
	 * @param parent
	 */
	public CommonClassLoader(ClassLoader parent) {
		super(parent);
	}

	/**
	 * @param fs
	 */
	public synchronized void addEntries(File[] fs) {
		this.entries.addAll(Arrays.asList(fs));
	}

	public synchronized void clear() {
		this.entries.clear();
	}

	/**
	 * @param f
	 */
	public synchronized void addEntry(File f) {
		this.entries.add(f);
	}
	/**
	 * @param fs
	 */
	public synchronized void removeEntry(File f){
		this.entries.remove(f);
	}

	public String toString() {
		String buf = "";
		for (File file : entries) {
			buf += file.getAbsolutePath() + ";";
		}
		return buf;
	}

	/**
	 * 加载class </br> todo 是否需做缓存、仔细仿照父loadClass的过程、使用参数b
	 */
	@SuppressWarnings("unchecked")
	protected Class loadClass(String name, boolean b)
			throws ClassNotFoundException {

		// 优先从父加载器获取
		Class clazz = null;
		try {
			clazz = super.loadClass(name, b);
			if (clazz != null) {
				return clazz;
			}
		} catch (ClassNotFoundException e) {
		}

		// System.out.println("DynaClassLoader:load class->" + name);

		int dotIndex = name.indexOf(".");

		String separator = File.separator;
		if (separator.equals("\\")) {
			separator = "\\\\";
		}

		String fileName;
		if (dotIndex != -1) {
			fileName = new StringBuilder()
					.append(name.replaceAll("\\.", separator)).append(".class")
					.toString();
		} else {
			fileName = new StringBuilder().append(name).append(".class")
					.toString();
		}

		InputStream is = getLocalResourceAsStream(fileName);
		if (is == null) {
			throw new ClassNotFoundException(name);
		}

		try {
			Class loadedClass = readClass(name, is);
			is.close();
			if (loadedClass != null) {
				return loadedClass;
			} else {
				throw new ClassNotFoundException(name);
			}
		} catch (Exception e) {
			throw new ClassNotFoundException(name);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
		}

	}

	private Class readClass(String className, InputStream is)
			throws IOException {
		BufferedInputStream bis = new BufferedInputStream(is);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] bytes = new byte[1024 * 10];
		int readBytes;
		while ((readBytes = bis.read(bytes)) != -1) {
			os.write(bytes, 0, readBytes);
		}
		byte[] b = os.toByteArray();
		return defineClass(className, b, 0, b.length);
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	private URL getLocalResource(String name) {

		for (int i = 0; i < entries.size(); i++) {
			File entry = entries.get(i);
			if (entry.isDirectory() && entry.exists()) {
				File f = new File(entry, name);
				if (f.exists()) {
					URL url;
					try {
						url = f.toURI().toURL();

					} catch (MalformedURLException ex) {
						continue;
					}
					return url;
				}
			} else if (entry.isFile() && entry.exists()) {
				URL url = null;
				try {
					ZipFile zf = new ZipFile(entry);
					name = name.replaceAll("\\\\", "/");
					ZipEntry zipEntry = zf.getEntry(name);
					if (zipEntry == null) {
						continue;
					}
					String url_0_ = entry.getAbsolutePath().replaceAll("\\\\",
							"/");
					if (!url_0_.startsWith("/")) {
						url_0_ = new StringBuilder().append("/").append(url_0_)
								.toString();
					}
					url = new URL(new StringBuilder().append("jar:file://")
							.append(url_0_).append("!/").append(name)
							.toString());
				} catch (ZipException zipexception) {
					zipexception.printStackTrace();
				} catch (IOException ioexception) {
					ioexception.printStackTrace();
				}
				return url;
			}
		}
		return null;
	}

	private InputStream getLocalResourceAsStream(String name) {
		URL res = getLocalResource(name);
		if (res == null) {
			return null;
		}
		InputStream inputstream = null;
		try {
			inputstream = res.openStream();
		} catch (IOException ex) {
			ex.printStackTrace();

		}
		return inputstream;
	}

	/**
	 * 加载数据流
	 */
	public InputStream getResourceAsStream(String name) {
		InputStream _super = super.getResourceAsStream(name);
		return _super != null ? _super : getLocalResourceAsStream(name);
	}

	/**
	 * 加载资源
	 */
	public URL getResource(String name) {
		URL resource = super.getResource(name);
		return resource != null ? resource : getLocalResource(name);
	}

	public static void main(String[] args) {
		CommonClassLoader commonClassLoader = new CommonClassLoader(Thread
				.currentThread().getContextClassLoader());
		commonClassLoader.addEntry(new File(
				"E:\\sbsworkspace\\easoustat_0.0.4\\target\\stat-no-lib.jar"));
		Thread.currentThread().setContextClassLoader(commonClassLoader);

		try {
			Class<?> c = Thread.currentThread().getContextClassLoader()
					.loadClass("com.easou.stat.cobcontainer.dao.DaoService");
			Class<?> x = Thread.currentThread().getContextClassLoader().loadClass("com.easou.stat.mapreduce.basic.salechannel.SaleChannelMapReducea");
			if (null != c) {
				Object o = c.newInstance();
				Method[] m = c.getDeclaredMethods();
				for (Method me : m) {
					// me.invoke(o, args)
					System.out.println(me.getName());
				}

				try {
					Method mm = c.getDeclaredMethod("main", String[].class);
					System.out.println(mm.getName());
					mm.invoke(o, (Object) new String[] {});
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}