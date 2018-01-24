package com.mcx.trk;


import io.github.binaryfoo.DecodedData;
import io.github.binaryfoo.RootDecoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Servlet implementation class TRKGenerator
 */
@WebServlet("/TRKGenerator")

public class TRKGenerator extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TRKGenerator() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("get qr codes from file");
		Part filePart = request.getPart("file"); // Retrieves <input type="file" name="file">
	    String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // MSIE fix.
	    InputStream fileContent = filePart.getInputStream();
	    DataReader dataReader = new DataReader(fileContent);
	    response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=TRK_Generated.xls");
		int noOfRows=dataReader.getRowCount("sh");
		//System.setProperty("webdriver.chrome.driver","chromedriver.exe");
		for(int i=2;i<=noOfRows;i++){			 
			String result=dataReader.getCellData("sh","QR",i);
			char[] chr=Hex.encodeHex(Base64.decodeBase64(result));
			List<DecodedData> decoded = new RootDecoder().decode(new String(chr), "EMV", "constructed");
			if(decoded.size()!=0){
				String trk=(String)decoded.get(decoded.size()-2).component3();
				System.out.println(trk);
				dataReader.setCellData("sh","TRK",i,trk.toLowerCase());
				
			}
		}
		XSSFWorkbook workbook=dataReader.workbook;
		ServletOutputStream out = response.getOutputStream();
		workbook.write(out);
        out.flush();
        out.close();
        
        
			
			
		
			
	}

}
