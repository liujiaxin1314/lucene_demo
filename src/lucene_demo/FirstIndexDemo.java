package lucene_demo;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class FirstIndexDemo {
	public static void main(String[] args) {
		try {
			/*testCreateIndex();
			System.out.println("创建索引完毕！！！");*/
			
			/*testSearchIndex();
			System.out.println("查询索引完毕！！！");*/
			
			testAddDocument();
			System.out.println("添加文档到索引库完毕！！！");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 测试创建索引的方法
	 * @throws Exception 
	 */
	public static void testCreateIndex() throws Exception{
		//创建一个Directory对象，指定索引库存放的位置。可以放到内存中也可以放到磁盘上。一般都是保存到磁盘上。
		//放到内存中
		//Directory directory = new RAMDirectory();
		//放到磁盘上
		Directory directory  = FSDirectory.open(new File("E:\\lucene"));
		//创建一个IndexWriter对象。需要指定两个参数，一个Directory对象一个是分析器对象。
		Analyzer analyzer = new StandardAnalyzer();
		//第一个参数Lucene对应的版本号
		//第二个参数分析器对象
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
		IndexWriter indexWriter = new IndexWriter(directory,config);
		//循环读取D:/传智播客/01.课程/04.lucene/01.参考资料/searchsource目录下的文档。
		File dir = new File("E:\\pachong\\text");
		for(File f :dir.listFiles()){
			//取文件名
			String fileName = f.getName();
			//文件路径
			String filePath = f.getAbsolutePath();
			//文件内容
			String fileContent = FileUtils.readFileToString(f);
			//文件大小
			long fileSize = FileUtils.sizeOf(f);
			//创建一个Document对象。
			Document document = new Document();
			//第一个参数：域的名称
			//第二个参数：域的内容
			//第三个参数：是否存储
			Field fileNameField = new TextField("title",fileName,Store.YES);
			Field fileContentField = new TextField("content",fileContent,Store.NO);
			Field filePathField = new StoredField("path",filePath);
			Field fileSizeField = new LongField("size",fileSize,Store.YES);
			//向Document对象中添加域。
			document.add(fileNameField);
			document.add(fileContentField);
			document.add(filePathField);
			document.add(fileSizeField);
			//把文档写入索引库
			indexWriter.addDocument(document);
		}
		//关闭IndexWriter对象
		indexWriter.close();
	}
	
	/**
	 * 测试查询索引库的方法
	 * @throws Exception
	 */
	public static void testSearchIndex() throws Exception{
		//创建一个Directory对象。指定索引库存放的目录	
		Directory directory = FSDirectory.open(new File("E:\\lucene"));
		//创建一个IndexReader对象。打开索引库
		IndexReader indexReader = DirectoryReader.open(directory);
		//创建一个IndexSearcher对象，构造方法中需要IndexReader对象。
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		//创建一个Query对象。两个参数：要搜索的域及要搜索的关键词。
		Query query = new TermQuery(new Term("content","个"));
		//IndexSearcher执行查询。
		//第一个参数查询对象，第二个参数返回结果的记录数
		TopDocs topDocs = indexSearcher.search(query, 10);
		//取查询结果的总记录数
		System.out.println("查询记录的总记录数："+topDocs.totalHits);
		//得到一个文档id列表。
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		//根据id取文档对象。
		for (ScoreDoc scoreDoc : scoreDocs) {
			//文档对应的id
			int id = scoreDoc.doc;
			//根据id取Document对象
			Document document = indexSearcher.doc(id);
			//从文档对象中取域的内容，展示给用户。
			System.out.println(document.get("title"));
			System.out.println(document.get("content"));
			System.out.println(document.get("size"));
			System.out.println(document.get("path"));
			System.out.println("----------------------------");
		}
		//关闭IndexReader。
		indexReader.close();
	}
	
	/**
	 * 索引库的添加
	 */
	public static void testAddDocument(){
		try {
			//索引库存放路径
			Directory directory = FSDirectory.open(new File("E:\\lucene"));
			IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, new IKAnalyzer());
			//创建一个indexwriter对象
			IndexWriter indexWriter = new IndexWriter(directory, config);
			//创建一个Document对象
			Document document = new Document();
			//向document对象中添加域。
			//不同的document可以有不同的域，同一个document可以有相同的域。
			document.add(new TextField("filename","新添加的文档",Store.YES));
			document.add(new TextField("content", "新添加的文档的内容",Store.NO));
			document.add(new TextField("content", "新添加的文档内容-第二个",Store.YES));
			document.add(new TextField("content1", "新添加的文档内容应该能看到",Store.YES));
			//添加文档到索引库
			indexWriter.addDocument(document);
			indexWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
}
