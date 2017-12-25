/*
<<20171113>> Import NCFTP tool for HRID transferring loss by Cola.
RealTimeLog transfer add Device: BJC10720,BM10720 , add MTK Series L449 upload STDF to OT 20171020
RealTimeLog transfer add Device: AN10757 20171017
L124 FT d10 & dls upload, only send FT naming file. by Cola. 20170913
RealTimeLog transfer add Device: AHH10176 by Circle. 20170725
F137 send STDF to MES for checking STDF transfer by Cola. 20170724
F152 dont send to Tango server by Cola. 20170720
RealTimeLog transfer add Device: AO10668 by Circle. 20170707
MTK Cancel Send to OT+ , by Tango upload. 20170621 by Cola.
RealTimeLog transfer add Device:AN10668 by Circle. 20170616
RealTimeLog transfer add Device:AD10363 by Cola. 20170612
RealTimeLog transfer add Device:BJC10299 by Cola. 20170609
RealTimeLog transfer add Device:BM10299 by Cola. 20170526
RealTimeLog transfer add Device:AN10569 by Cola. 20170511
20170405 Revise TransferRealTimeLogforHRID code for data loss and CORR data don't sent.
MTK STDF all upload to Tango. 20170327
RealTimeLog transfer add Device:BN10313 by Cola. 20170324
Add F154 STDF upload by Cola. 20170320
RealTimeLog transfer add Device:AN10587 by Cola. 20170317
F137 transfer STDF by test station by Cola. 20170316
L124 transfer dls & d10 file to 192.168.1.5 by Cola. 20170316
L124 copy file change single file transfer. 20161213
MTK OP+ check folder file is .std.gz file. 20161121
Adjust file transfer delay time for MTK CP STDF to OP+ and L124 don't send STDF. 20161108
Adjust file transfer number of amount for MTK CP STDF to OP+ 20161107
Version3 transfer F186 InfoFile to IT server
ReWrite RealTimeLog Upload code and add L129 BHH10386 log upload by Cola. 20160811
*/
import java.io.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import java.lang.String.*;
import java.lang.Object.*;
import java.lang.Process.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import java.net.*;
/*
\\192.168.1.70\servulog -> 此位置可查詢Eric對外排程之拋送Log
*/
public class D10_STDF_upload_ALL extends JFrame{

	static String cmdStr = "";
	static String[] UploadRealTimeLog_Device_ForMTK = {"10060","AM10168","AJC10168","AN10363","AE10363","AW10363","BHH10386","AN10587","10313","AN10569","BM10299", "BJC10299", "AD10363", "AN10668", "AO10668", "AHH10176", "AN10757", "BJC10720", "BM10720"}; //Add by Cola. 20160811
//	String[] L022_10060_realTimeLog_upload = {"192.168.1.76","sghpe2","sgh555pe2","/usr/local/home/vol5/RealTimelog/L022/CP/10060/","/HRID/BM10060",".txt.gz"};  
//	String[] L129_AM10168_realTimeLog_upload = {"192.168.1.76","sghpe2","sgh555pe2","/usr/local/home/vol5/RealTimelog/L129/CP/AM10168/","/HRID/AM10168",".txt.gz"}; 
//	String[] L129_AJC10168_realTimeLog_upload = {"192.168.1.76","sghpe2","sgh555pe2","/usr/local/home/vol5/RealTimelog/L129/CP/AJC10168/","/HRID/AJC10168",".txt.gz"}; 
//	String[] L129_AN10363C_realTimeLog_upload = {"192.168.1.76","sghpe2","sgh555pe2","/usr/local/home/vol5/RealTimelog/L129/CP/AN10363/","/HRID/AN10363C",".txt.gz"};
//	String[] L129_AE10363C_realTimeLog_upload = {"192.168.1.76","sghpe2","sgh555pe2","/usr/local/home/vol5/RealTimelog/L129/CP/AE10363/","/HRID/AE10363C",".txt.gz"}; 
//	String[] L129_AW10363_realTimeLog_upload = {"192.168.1.76","sghpe2","sgh555pe2","/usr/local/home/vol5/RealTimelog/L129/CP/AW10363/","/HRID/AW10363",".txt.gz"};
	static String L010_tar_gz_fileName = "";
	static String innitial_path = "/usr/local/home/vol5/MAP/";
	static String innitial_path_L010_upload = "/usr/local/home/vol5/STDF/L010/CP/";
	static String innitial_path_STDF = "/usr/local/home/vol5/STDF/";
	static String innitial_path_STDF_FT = "/usr/local/home/vol4/D10/prod/autoResult/summary/autosend/";                
	static String innitial_path_add_cusNO = "";
	static String innitial_path_add_cusNO_STDF = "";
	static String innitial_path_add_cusNO_STDF_FT = "";  
	static String innitial_path_L010_add_cusNO = "";
	static boolean check_L010_content_flag = false; 
	static boolean check_CP_stdf_content_flag = false;           
	static boolean check_FT_stdf_content_flag = false;
	static boolean check_CP_AZA10176A_stdf_content_flag = false;
	static boolean check_CP_BHD10238_stdf_content_flag = false;
	static boolean check_CP_AHD10289_stdf_content_flag = false;       
	static boolean check_CP_F137_stdf_content_flag = false;   
	static boolean check_CP_L285_stdf_content_flag = false; 
	static boolean check_CP_L022_MTK_stdf_content_flag = false;
	static boolean check_CP_L129_MTK_stdf_content_flag = false;
	static boolean check_CP_L449_MTK_stdf_content_flag = false; //20171020
//	static boolean check_CP_L124_stdf_content_flag = false;
	static int forloopNO = 0;                                     
	static String[] cusNO_array;
	static String[] STDF_cusNO_array;
	static String[] STDF_cusNO_array_FT;        
	static String[] device_array;
	static String[] STDF_device_array;
	static String[] L022_STDF_device_array;
	static String[] L129_STDF_device_array;
	static String[] L449_STDF_device_array; //20171020
	static String[] L010_device_array; 
	static String[] L010_folder_content_array;  //確認資料夾內是否有.map .rep...檔案
	static String[] CP_stdf_folder_content_array;  //確認資料夾內是否有.std檔案
	static String[] FT_stdf_folder_content_array;  //確認資料夾內是否有.std檔案
	static String[] CP_AZA10176A_stdf_folder_content_array;  //確認資料夾內是否有.std檔案   
	static String[] CP_BHD10238_stdf_folder_content_array;  //確認資料夾內是否有.std檔案
	static String[] CP_AHD10289_stdf_folder_content_array;  //確認資料夾內是否有.std檔案
	static String[] CP_F137_stdf_folder_content_array;  //確認資料夾內是否有.std檔案     
	static String[] CP_L285_stdf_folder_content_array;  //確認資料夾內是否有.dat檔案
	static String[] L124_stdf_folder_content_array;  //確認資料夾內是否有.dat檔案            
	static String[] CP_L022_MTK_stdf_folder_content_array;  //check folder's std file	
	static String[] CP_L129_MTK_stdf_folder_content_array;  //check folder's std file
	static String[] CP_L449_MTK_stdf_folder_content_array;  //check folder's std file //20171020
	static String[] CP_summary_array;  //確認資料夾內是否有.sum檔案                                         
	static String final_path = "";
	static String STDF_local_path1 = "/usr/local/home/vol5/stdf_ftp/";
	static String STDF_local_path2 = "/usr/local/home/vol5.new/stdf_ftp/";
	static String[] F071_STDF_upload1 = {"192.168.200.6","maxlinear","fjk0H8Y~","/usr/local/home/prod/RealTimelog/F071/MXL584/","/CP/TestData/MXL584_STDFile/",".std.gz"}; 
	static String[] F071_STDF_upload2 = {"192.168.200.6","maxlinear","fjk0H8Y~","/usr/local/home/prod/RealTimelog/F071/GINJO_V1/","/CP/TestData/GINJO_V1_STDFile/",".std.gz"};
	static String[] F071_STDF_upload3 = {"192.168.200.6","maxlinear","fjk0H8Y~","/usr/local/home/prod/RealTimelog/F071/Minotaur_V9/","/CP/TestData/Minotaur_V9_STDFile/",".std.gz"};
	static String[] F071_STDF_upload4 = {"192.168.200.6","maxlinear","fjk0H8Y~","/usr/local/home/prod/RealTimelog/F071/MxL267/","/CP/TestData/MXL267V1_STDFile/",".std.gz"};
	static String[] F071_STDF_upload5 = {"192.168.200.6","maxlinear","fjk0H8Y~","/usr/local/home/prod/RealTimelog/F071/MXL581/","/CP/TestData/MXL581_STDFile/",".std.gz"};
	static String[] F071_STDF_upload6 = {"192.168.200.6","maxlinear","fjk0H8Y~","/usr/local/home/prod/RealTimelog/F071/Thor_v2/","/CP/TestData/Thor_V2_STDFile/",".std.gz"};
	static String[] F071_STDF_upload7 = {"192.168.200.6","maxlinear","fjk0H8Y~","/usr/local/home/prod/RealTimelog/F071/MXL568/","/CP/TestData/MXL568_STDFile/",".std.gz"}; 
	static String[] F071_STDF_upload8 = {"192.168.200.6","maxlinear","fjk0H8Y~","/usr/local/home/prod/RealTimelog/F071/MXL267E0/","/CP/TestData/MXL267E0_STDFile/",".std.gz"}; 
	static String[] L389_STDF_upload = {"192.168.200.6","loader","loader","/usr/local/home/vol5/stdf_ftp/","/testerdata/L022/STDF/","HL389"}; 
	//        String[] MT6589_STDF_upload = {"192.168.1.21","loader","loader","/usr/local/home/vol5/STDF/L022/CP/10057/","/testerdata/L022/STDF/",".std.gz"};
	//        String[] AN10159_STDF_upload = {"192.168.1.21","loader","loader","/usr/local/home/vol5/STDF/L022/CP/10159/","/testerdata/L022/STDF/",".std.gz"};
//	static String[] MT6589_STDF_upload = {"192.168.1.21","loader","loader","/usr/local/home/vol5/STDF/L022/CP/10057/","/testerdata/L022/STDF/SGH/CP/",".std.gz"};
//	static String[] BHH67208_STDF_upload = {"192.168.1.21","loader","loader","/usr/local/home/vol5/STDF/L022/CP/BHH67208/","/testerdata/L022/STDF/SGH/CP/",".std.gz"};        
//	static String[] AN10159_STDF_upload = {"192.168.1.21","loader","loader","/usr/local/home/vol5/STDF/L022/CP/10159/","/testerdata/L022/STDF/SGH/CP/",".std.gz"};
	static String[] AZA10176A_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/STDF/L022/CP/AZA10176A/","/L022_GLOBAL/From_Sigurd/STDF_data",".std.gz"};
	static String[] BHD10238_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/STDF/L022/CP/10238/","/L022/TMFQ18_BHD10238/",".std.gz"};
	static String[] AHD10289_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/STDF/L022/CP/10289/","/L022/TMFJ04_AHD10289/",".std.gz"};
	static String[] TSMC_BHD10238_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/STDF/L022/CP/10238/","/L022/TSMC_BHD10238/",".std.gz"};
	static String[] TSMC_AHD10289_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/STDF/L022/CP/10289/","/L022/TSMC_AHD10289/",".std.gz"}; 
	static String[] SSMC_AHD10289_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/STDF/L022/CP/10289/","/L022/SSMC_AHD10289/","TS"};	//SSMC只傳客批TS開頭或TTS開頭
	static String[] F137_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/STDF/F137/STDF_transfer/transfer_tmp/","/F137/data/from_sigurd/STDF/",".std.gz"};  
	static String[] L285_STDF_upload = {"192.168.1.5","L285","d_SD5u9inz","/usr/local/home/vol4/D10/prod/autoResult/summary/L285/Summary/temp/","/CP/SUMMARY/",".dat.gz"};                                      
	static String[] L178_CP_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/RealTimelog/L178/Temp/","/L178/CP/",".gz"};
	static String[] L178_FT_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/RealTimelog/L178/Temp_FT/","/L178/FT/",".gz"};
	//			String[] Example = {"IP","FTP user","FTP pw","local address","server address","upload file keyword"};
	static String[] L022_STDF_upload = {"192.168.1.21","loader","loader","/usr/local/home/vol5/stdf_ftp/","/testerdata/L022/STDF/","*HL022*.gz"};
	static String[] L129_STDF_upload = {"192.168.1.21","loader","loader","/usr/local/home/vol5/stdf_ftp/","/testerdata/L022/STDF/","*HL129*.gz"};   
	static String[] F054_STDF_upload = {"192.168.1.21","loader","loader","/usr/local/home/vol5/stdf_ftp/","/testerdata/L022/STDF/","*HF054*.gz"};
	static String[] L389_STDF_upload2 = {"192.168.1.21","loader","loader","/usr/local/home/vol5/stdf_ftp/","/testerdata/L022/STDF/","*HL389*.gz"}; 
	          
	static String[] F128_STDF_upload = {"192.168.220.11","hk_smsc","pKamn46B","/usr/local/home/vol4/D10/prod/autoResult/summary/autosend/F128/","/To_SMSC_Production_HK/Prod_stdf/",".gz"}; 
	static String[] F152_data_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/stdf_backup/F152/","/F152/ProductionData/",".zip"};
	static String[] L405_data_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/MAP/L405/7376/dat/","/L405/dat_file/","GLI7376G.dat"};
	static String[] F126_UGO001B110_WebM_STDF_upload = {"192.168.36.72","Verisiliconftp","8aynLF+f","/usr/local/home/vol5/STDF/F126/CP/WEBM/","/STDF/UGO001B110_WebM/",".std.gz"};
	static String[] F126_JMA001B110_Frizz_STDF_upload = {"192.168.36.72","Verisiliconftp","8aynLF+f","/usr/local/home/vol5/STDF/F126/CP/FRIZZ/","/STDF/JMA001B110_Frizz/",".std.gz"};
	static String[] L416_FT_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/stdf_backup/L416/","/L416/FT/",".std.gz"};
	static String[] F089_FT_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/stdf_backup/F089/","/L416/FT/",".std.gz"};
//	String[] L416_CP_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/STDF/L416/","/L416/CP/",".std.gz"};
//	String[] F089_CP_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/STDF/F089/","/L416/CP/",".std.gz"};    
//	static String[] L022_MTK_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/STDF/L022/CP/","/L129/MTK_D10_L129_CP_all_STDF/",".std.gz"};
//	static String[] L129_MTK_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/STDF/L129/CP/","/L129/MTK_D10_L129_CP_all_STDF/",".std.gz"};
	static String[] L022_MTK_STDF_upload_tango = {"192.168.1.21","loader","loader","/usr/local/home/vol5/STDF/L022/CP/","/testerdata/L022/STDF/SGH/CP/",".std.gz"};
	static String[] L129_MTK_STDF_upload_tango = {"192.168.1.21","loader","loader","/usr/local/home/vol5/STDF/L129/CP/","/testerdata/L129/STDF/SGH/CP/",".std.gz"};
	static String[] L449_MTK_STDF_upload_tango = {"192.168.1.21","loader","loader","/usr/local/home/vol5/STDF/L449/CP/","/testerdata/L449/STDF/SGH/CP/",".std.gz"}; //20171020
	static String[] L124_File_upload_CP = {"192.168.1.5","asipe2","asi555pe2","/usr/local/home/vol5/STDF/L124/Data_transfor/","/customer/L124/Datalog/",".d10"};
	static String[] L124_File_upload_FT = {"192.168.1.5","asipe2","asi555pe2","/usr/local/home/vol5/stdf_backup/L124/Data_transfor/","/customer/L124/FT/DataLog/",".d10"}; 
	static String[] F154_CP_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/STDF/F154/","F154/cp/stdf",".std.gz"};	//20170320
	static boolean AZA10176A_Upload_RunEver,BHD10238_Upload_RunEver,AHD10289_Upload_RunEver;
	
	public static void javaExecSystemCmd2(String cmdStr, int waittime) {

		String tmpStr = null;

		try {
			Process proc = Runtime.getRuntime().exec(cmdStr);
			Thread.sleep(waittime);// unit: ms

			tmpStr = "Complete cmd: " + cmdStr + "\n\n";
			System.err.print(tmpStr);

		} catch (java.io.IOException err) {
			tmpStr = "<Exception1> javaExecSystemCmd2: " + err + "\n";
			System.err.print(tmpStr);
		} catch (java.lang.InterruptedException Ierr) {
			tmpStr = "<Exception2> javaExecSystemCmd2: " + Ierr + "\n";
			System.err.print(tmpStr);
		}
	}
	//=================RealTimelog Upload Founction======================start
//	String[] L129_AM10168_realTimeLog_upload = {"192.168.1.76","sghpe2","sgh555pe2","/usr/local/home/vol5/RealTimelog/L129/CP/AM10168/","/HRID/AM10168",".txt.gz"}; 
	static void TransferRealTimeLogforHRID(String DeviceName){
		String[] CP_RealTimelog_Device_array;  //Search Device
		String[] CP_RealTimelog_File_array;  //check .txt file
		String[] CP_CustomerID = {"L022","L129","L449"}; // add L449 20171020
		String SourcePath = "";
		String UploadPath = "";
		boolean createDirFlag;

		for(int c = 0; c < CP_CustomerID.length; c++){
			File CP_RealTimelog_customer_folder_content = new File("/usr/local/home/vol5/RealTimelog/" + CP_CustomerID[c] + "/CP");
			CP_RealTimelog_Device_array = CP_RealTimelog_customer_folder_content.list();

			for (int d = 0; d < CP_RealTimelog_Device_array.length; d++){  //Search Device Folder
				if(CP_RealTimelog_Device_array[d].indexOf(DeviceName) != -1){
					SourcePath = "/usr/local/home/vol5/RealTimelog/" + CP_CustomerID[c] + "/CP/" + CP_RealTimelog_Device_array[d] +"/";
					UploadPath = "/HRID/" + DeviceName + "/";
                    if(DeviceName.equals("10060")) UploadPath = "/HRID/BM10060/";
                    if(DeviceName.equals("10313")) UploadPath = "/HRID/BN10313/"; //20170324
                    
                    File CP_RealTimelog_Current_folder = new File(SourcePath);
					if (CP_RealTimelog_Current_folder.isDirectory()){//check .txt file
						System.out.println("RealTimelog " + CP_RealTimelog_Device_array[d] + " Upload !!");
						createDirFlag = false;
						CP_RealTimelog_File_array = CP_RealTimelog_Current_folder.list();
						for (int f = 0; f < CP_RealTimelog_File_array.length; f++){
							if(CP_RealTimelog_File_array[f].indexOf(".txt.gz") != -1){ //20170327
								if(!createDirFlag){
									createDirFlag = true;
									cmdStr = "mkdir " + SourcePath + "backup";
									javaExecSystemCmd2(cmdStr,1000);
									cmdStr = "chmod 777 " + SourcePath + "backup";
									javaExecSystemCmd2(cmdStr,1000);
									cmdStr = "mkdir " + SourcePath + "Transfer_temp";
									javaExecSystemCmd2(cmdStr,1000);
									cmdStr = "chmod 777 " + SourcePath + "Transfer_temp";
									javaExecSystemCmd2(cmdStr,1000);
									cmdStr = "java_Regular_Expression.csh " + SourcePath + " .txt.gz " + SourcePath + "Transfer_temp/";
									javaExecSystemCmd2(cmdStr,10000);
								}
							}
						}
						if(createDirFlag){
							CP_RealTimelog_Current_folder = new File(SourcePath + "Transfer_temp/"); //20170405
							CP_RealTimelog_File_array = CP_RealTimelog_Current_folder.list();

							for (int f = 0; f < CP_RealTimelog_File_array.length; f++){	//改為傳送*.txt.gz 以改善data loss 問題
								if(CP_RealTimelog_File_array[f].indexOf("-CORR") != -1){ //don't send CORR data. 20170324
									cmdStr = "java_Regular_Expression.csh " + SourcePath + "Transfer_temp/" + " " + CP_RealTimelog_File_array[f] +" " + SourcePath + "backup/";
									javaExecSystemCmd2(cmdStr,5000);
								}
							}
							/*	20171113 Remark-----Start
							cmdStr = "ftp_sigurd_cp_all.csh 192.168.1.76 sghpe2 sgh555pe2 " + SourcePath + "Transfer_temp/" + " " + UploadPath + " .txt.gz";					
							javaExecSystemCmd2(cmdStr,20000);
							javaExecSystemCmd2(cmdStr,20000);

							cmdStr = "java_Regular_Expression.csh " + SourcePath + "Transfer_temp/" + " .txt.gz " + SourcePath + "backup/";
							javaExecSystemCmd2(cmdStr,1000);
								20171113 Remark-----End		*/
							
							//20171113 ncftp use-----Start
							javaExecSystemCmd2("java_Regular_Expression_command4.csh cp " + SourcePath + "Transfer_temp/ " + ".txt.gz " + SourcePath + "backup/",5000);
							//cmdStr = "ncftpput -DD -u sghpe2 -p sgh555pe2 192.168.1.76 " + UploadPath + " " + SourcePath + "Transfer_temp/*.txt.gz";
							cmdStr = "ftp_sigurd_cp_all_NCFTP.csh 192.168.1.76 sghpe2 sgh555pe2 " + SourcePath + "Transfer_temp/" + " " + UploadPath + " .txt.gz";
							javaExecSystemCmd2(cmdStr,20000);
							//20171113 ncftp use-----End
						}
					}	
				}
			}
		}
	}//=================RealTimelog Upload Function======================end
	static void AZA10176A_Upload(String UploadFileName){
		//=================AZA10176A STDF upload======================start
		//AZA10176A_STDF_upload = {"203.117.92.22","cs-sigurd","2ynq9csqd6","/usr/local/home/vol5/STDF/L022/CP/AZA10176A/","/to_gf/yms/mtk_cp/AZA10176_STDF/",".std.gz"};			
//		File CP_AZA10176A_stdf_folder_content_array_file = new File("/usr/local/home/vol5/STDF/L022/CP/AZA10176A/");
//		if (CP_AZA10176A_stdf_folder_content_array_file.isDirectory()){//check 檔案內有無.std檔案，有才去執行				            							
//
//			CP_AZA10176A_stdf_folder_content_array = CP_AZA10176A_stdf_folder_content_array_file.list();	
//			for (int k = 0; k < CP_AZA10176A_stdf_folder_content_array.length; k++){
//				if(CP_AZA10176A_stdf_folder_content_array[k].indexOf(".std.gz") != -1)
//					check_CP_AZA10176A_stdf_content_flag = true;
//
//				if (check_CP_AZA10176A_stdf_content_flag == true){				            										            						
				if(AZA10176A_Upload_RunEver == false){
					AZA10176A_Upload_RunEver = true;
					cmdStr = "java_Regular_Expression.csh " + AZA10176A_STDF_upload[3] + " " + "EQC " + AZA10176A_STDF_upload[3] + "backup";
					javaExecSystemCmd2(cmdStr,3000);								

					cmdStr = "ftp_sigurd_cp_all.csh";
					for (int i = 0; i < AZA10176A_STDF_upload.length; i++)        	
						cmdStr = cmdStr + " " + AZA10176A_STDF_upload[i];						

					javaExecSystemCmd2(cmdStr,10000);
				}
//					cmdStr = "java_Regular_Expression.csh " + AZA10176A_STDF_upload[3] + " " + AZA10176A_STDF_upload[5] + " " + "/usr/local/home/vol5/STDF/L022/CP/AZA10176A/backup/";
//					javaExecSystemCmd2(cmdStr,5000); 
//					break;
//				}
//			}
//		}
	}
	static void BHD10238_Upload(String UploadFileName){
		//=================10238 STDF upload======================start
		//BHD10238_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/STDF/L022/CP/10238/","/L022/TMFQ18_BHD10238/",".std.gz"};
		//TSMC_BHD10238_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/STDF/L022/CP/10238/","/L022/TSMC_BHD10238/",".std.gz"};

//		File CP_BHD10238_stdf_folder_content_array_file = new File("/usr/local/home/vol5/STDF/L022/CP/10238/");
//		if (CP_BHD10238_stdf_folder_content_array_file.isDirectory()){//check 檔案內有無.std檔案，有才去執行				            							
//
//			CP_BHD10238_stdf_folder_content_array = CP_BHD10238_stdf_folder_content_array_file.list();	
//			for (int k = 0; k < CP_BHD10238_stdf_folder_content_array.length; k++){
//				if(CP_BHD10238_stdf_folder_content_array[k].indexOf(".std.gz") != -1)
//					check_CP_BHD10238_stdf_content_flag = true;
//
//				if (check_CP_BHD10238_stdf_content_flag == true){				            										            						
				if(BHD10238_Upload_RunEver == false){
					BHD10238_Upload_RunEver = true;
					cmdStr = "java_Regular_Expression.csh " + BHD10238_STDF_upload[3] + " " + "EQC " + BHD10238_STDF_upload[3] + "backup";
					javaExecSystemCmd2(cmdStr,3000);								

					cmdStr = "ftp_sigurd_cp_all.csh";
					for (int i = 0; i < BHD10238_STDF_upload.length; i++)        	
						cmdStr = cmdStr + " " + BHD10238_STDF_upload[i];						

					javaExecSystemCmd2(cmdStr,10000);

					cmdStr = "ftp_sigurd_cp_all.csh";
					for (int i = 0; i < TSMC_BHD10238_STDF_upload .length; i++)     //20150205 autoSent to TSMC by ChiaHui    	
						cmdStr = cmdStr + " " + TSMC_BHD10238_STDF_upload [i];						

					javaExecSystemCmd2(cmdStr,10000);			        							        
				}
//					cmdStr = "java_Regular_Expression.csh " + BHD10238_STDF_upload[3] + " " + BHD10238_STDF_upload[5] + " " + "/usr/local/home/vol5/STDF/L022/CP/10238/backup/";
//					javaExecSystemCmd2(cmdStr,5000); 
//					break;
//				}
//			}
//		}
	}
	static void AHD10289_Upload(String UploadFileName){
		//=================10289 STDF upload======================start
		//AHD10289_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/STDF/L022/CP/10289/","/L022/TMFJ04_AHD10289/",".std.gz"};
		//TSMC_AHD10289_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/STDF/L022/CP/10289/","/L022/TSMC_AHD10289/",".std.gz"};
		//SSMC_AHD10289_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/STDF/L022/CP/10289/","/L022/SSMC_AHD10289/","TS"};

//		File CP_AHD10289_stdf_folder_content_array_file = new File("/usr/local/home/vol5/STDF/L022/CP/10289/");
//		if (CP_AHD10289_stdf_folder_content_array_file.isDirectory()){//check 檔案內有無.std檔案，有才去執行				            							
//
//			CP_AHD10289_stdf_folder_content_array = CP_AHD10289_stdf_folder_content_array_file.list();	
//			for (int k = 0; k < CP_AHD10289_stdf_folder_content_array.length; k++){
//				if(CP_AHD10289_stdf_folder_content_array[k].indexOf(".std.gz") != -1)
//					check_CP_AHD10289_stdf_content_flag = true;
//
//				if (check_CP_AHD10289_stdf_content_flag == true){				            										            						
				if(AHD10289_Upload_RunEver == false){
					AHD10289_Upload_RunEver = true;
					cmdStr = "java_Regular_Expression.csh " + AHD10289_STDF_upload[3] + " " + "EQC " + AHD10289_STDF_upload[3] + "backup";
					javaExecSystemCmd2(cmdStr,3000);								

					cmdStr = "ftp_sigurd_cp_all.csh";
					for (int i = 0; i < AHD10289_STDF_upload.length; i++)        	
						cmdStr = cmdStr + " " + AHD10289_STDF_upload[i];						

					javaExecSystemCmd2(cmdStr,5000);

					cmdStr = "ftp_sigurd_cp_all.csh";
					for (int i = 0; i < TSMC_AHD10289_STDF_upload .length; i++)     //20150205 autoSent to TSMC by ChiaHui    	
						cmdStr = cmdStr + " " + TSMC_AHD10289_STDF_upload [i];						

					javaExecSystemCmd2(cmdStr,5000);

					cmdStr = "ftp_sigurd_cp_all.csh";
					for (int i = 0; i < SSMC_AHD10289_STDF_upload .length; i++)     //20150313 autoSent to SSMC by ChiaHui (Chou_Roy)    	
						cmdStr = cmdStr + " " + SSMC_AHD10289_STDF_upload [i];			//20150408 autoSent to SSMC (只傳客批TS開頭的lot)

					javaExecSystemCmd2(cmdStr,5000);
				}
//					cmdStr = "java_Regular_Expression.csh " + AHD10289_STDF_upload[3] + " " + AHD10289_STDF_upload[5] + " " + "/usr/local/home/vol5/STDF/L022/CP/10289/backup/";
//					javaExecSystemCmd2(cmdStr,5000); 
//					break;
//				}
//			}
//		}		
	}
	/*
	static void MT10057_Upload(String UploadFileName){
		//=================MT6589 STDF upload======================start
		cmdStr = "java_Regular_Expression.csh " + MT6589_STDF_upload[3] + " " + "EQC " + MT6589_STDF_upload[3] + "backup";
		javaExecSystemCmd2(cmdStr,500);						

		cmdStr = "ftp_sigurd_cp_all.csh";
		for (int k = 0; k < MT6589_STDF_upload.length; k++)        	
			cmdStr = cmdStr + " " + MT6589_STDF_upload[k];						

		javaExecSystemCmd2(cmdStr,10000);
		cmdStr = "java_Regular_Expression.csh " + MT6589_STDF_upload[3] + " " + MT6589_STDF_upload[5] + " " + "/usr/local/home/vol5/STDF/L022/CP/10057/backup";
		javaExecSystemCmd2(cmdStr,5000); 
	}
	static void AN10159_Upload(String UploadFileName){
		//=================10159 STDF upload======================start
		cmdStr = "java_Regular_Expression.csh " + AN10159_STDF_upload[3] + " " + "EQC " + AN10159_STDF_upload[3] + "backup";
		javaExecSystemCmd2(cmdStr,500);	

		cmdStr = "ftp_sigurd_cp_all.csh";
		for (int k = 0; k < AN10159_STDF_upload.length; k++)        	
			cmdStr = cmdStr + " " + AN10159_STDF_upload[k];						

		javaExecSystemCmd2(cmdStr,10000);
		cmdStr = "java_Regular_Expression.csh " + AN10159_STDF_upload[3] + " " + AN10159_STDF_upload[5] + " " + "/usr/local/home/vol5/STDF/L022/CP/10159/backup";
		javaExecSystemCmd2(cmdStr,5000);
	}
	static void BHH67208_Upload(String UploadFileName){
		//=================BHH67208 STDF upload (20140929)======================start
		cmdStr = "java_Regular_Expression.csh " + BHH67208_STDF_upload[3] + " " + "EQC " + BHH67208_STDF_upload[3] + "backup";
		javaExecSystemCmd2(cmdStr,500);	

		cmdStr = "ftp_sigurd_cp_all.csh";
		for (int k = 0; k < BHH67208_STDF_upload.length; k++)        	
			cmdStr = cmdStr + " " + BHH67208_STDF_upload[k];						

		javaExecSystemCmd2(cmdStr,10000);
		cmdStr = "java_Regular_Expression.csh " + BHH67208_STDF_upload[3] + " " + BHH67208_STDF_upload[5] + " " + "/usr/local/home/vol5/STDF/L022/CP/BHH67208/backup";
		javaExecSystemCmd2(cmdStr,5000);
	}
*/	
	
	public static void main(String[] args) throws Exception
	{
		SimpleDateFormat nowdate = new java.text.SimpleDateFormat("yyyyMMddHHmm");
		nowdate.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		String TaiwanTime;	//now time
		
		
		File f = new File(innitial_path);
		File STDF_file = new File(innitial_path_STDF);       
		File STDF_file_FT = new File(innitial_path_STDF_FT);    
		File L010_upload = new File(innitial_path_L010_upload);   
		File MTK_L022_STDF_file = new File("/usr/local/home/vol5/STDF/L022/CP/");
		File MTK_L129_STDF_file = new File("/usr/local/home/vol5/STDF/L129/CP/");
		File MTK_L449_STDF_file = new File("/usr/local/home/vol5/STDF/L449/CP/");

		while(true){  
			//F137_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/STDF/F137/STDF_transfer/transfer_tmp/","/F137/data/from_sigurd/STDF/",".std.gz"};
			cmdStr = "java_Regular_Expression.csh " + "/usr/local/home/vol5/STDF/F137/STDF_transfer/" + " " + "corr " + F137_STDF_upload[3] + "backup";
			javaExecSystemCmd2(cmdStr,3000);								

			cmdStr = "java_Regular_Expression.csh " + "/usr/local/home/vol5/STDF/F137/STDF_transfer/" + " " + F137_STDF_upload[5] + " " + "/usr/local/home/vol5/STDF/F137/STDF_transfer/transfer_tmp/";
			javaExecSystemCmd2(cmdStr,3000); 
			File CP_F137_stdf_folder_content_array_file = new File("/usr/local/home/vol5/STDF/F137/STDF_transfer/transfer_tmp");
			if (CP_F137_stdf_folder_content_array_file.isDirectory()){//check 檔案內有無.std檔案，有才去執行				            							
	
				CP_F137_stdf_folder_content_array = CP_F137_stdf_folder_content_array_file.list();
				
				for (int k = 0; k < CP_F137_stdf_folder_content_array.length; k++){
					if(CP_F137_stdf_folder_content_array[k].indexOf(".std.gz") != -1)
						check_CP_F137_stdf_content_flag = true;

					if (check_CP_F137_stdf_content_flag == true){				            										            						
						if(CP_F137_stdf_folder_content_array[k].indexOf("CP1") != -1) //20170316
							F137_STDF_upload[4] = "/F137/data/from_sigurd/STDF/STDF_CP1";
						else if(CP_F137_stdf_folder_content_array[k].indexOf("CP2") != -1)
							F137_STDF_upload[4] = "/F137/data/from_sigurd/STDF/STDF_CP2";

						cmdStr = "ftp_sigurd_cp_all.csh";
						for (int i = 0; i < F137_STDF_upload.length; i++){
							if(i != 5)
								cmdStr += " " + F137_STDF_upload[i];
							else
								cmdStr += " " + CP_F137_stdf_folder_content_array[k];
						}
						javaExecSystemCmd2(cmdStr,5000);
//						{"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/STDF/F137/STDF_transfer/transfer_tmp/","/F137/data/from_sigurd/STDF/",".std.gz"};  
						javaExecSystemCmd2("ftp_sigurd_cp_all.csh 192.168.1.76 sghpe2 sgh555pe2 /usr/local/home/vol5/STDF/F137/STDF_transfer/transfer_tmp/ /customer/F137/STDF .std.gz",5000); //20170724
						cmdStr = "java_Regular_Expression.csh " + F137_STDF_upload[3] + " " + CP_F137_stdf_folder_content_array[k] + " " + "/usr/local/home/vol5/STDF/F137/STDF_transfer/backup/";
						javaExecSystemCmd2(cmdStr,1000); 

					}
				}
			}	
        
			 
			//=================L124 STDF upload======================start. Change single file update by Cola. 20161213 
			//        String[] L124_D10File_upload = {"192.168.1.5","asipe2","asi555pe2","/usr/local/home/vol5/STDF/L124/Data_transfor/","/customer/L124/Datalog/",".d10"};
			//        String[] L124_dlsFile_upload = {"192.168.1.5","asipe2","asi555pe2","/usr/local/home/vol5/STDF/L124/Data_transfor/","/customer/L124/Datalog/",".dls"}; 
			File CP_L124_stdf_folder_content_array_file = new File("/usr/local/home/vol5/STDF/L124/Data_transfor/");
			if (CP_L124_stdf_folder_content_array_file.isDirectory()){//check 檔案內有無.dat檔案，有才去執行

				L124_stdf_folder_content_array = CP_L124_stdf_folder_content_array_file.list();	
				for (int k = 0; k < L124_stdf_folder_content_array.length; k++){
					if(L124_stdf_folder_content_array[k].indexOf(".d10") != -1 || L124_stdf_folder_content_array[k].indexOf(".dls") != -1){
//						check_CP_L124_stdf_content_flag = true;
					
						cmdStr = "ftp_sigurd_cp_all.csh";

						for (int i = 0; i < L124_File_upload_CP.length; i++){       	
							if(i == 5)
								cmdStr = cmdStr + " " + L124_stdf_folder_content_array[k];
							else
								cmdStr = cmdStr + " " + L124_File_upload_CP[i];	
						}
						javaExecSystemCmd2(cmdStr,3000);
						javaExecSystemCmd2(cmdStr,3000); //Still random lose, double send command. 20161220
						cmdStr = "java_Regular_Expression.csh " + L124_File_upload_CP[3] + " " + L124_stdf_folder_content_array[k] + " " + "/usr/local/home/vol5/STDF/L124/Data_Backup/";
						javaExecSystemCmd2(cmdStr,2000);
					}
				}
			}         
		   //L124 FT data upload. 20170316-----Start
			cmdStr = "java_Regular_Expression.csh " + "/usr/local/home/vol5/stdf_backup/L124/Data_transfor/" + " " + "CORR " + "/usr/local/home/vol5/stdf_backup/L124/Data_Corr";
			javaExecSystemCmd2(cmdStr,3000);
			File FT_L124_stdf_folder_content_array_file = new File("/usr/local/home/vol5/stdf_backup/L124/Data_transfor");
			if (FT_L124_stdf_folder_content_array_file.isDirectory()){

				L124_stdf_folder_content_array = FT_L124_stdf_folder_content_array_file.list();	
				for (int k = 0; k < L124_stdf_folder_content_array.length; k++){
					if(L124_stdf_folder_content_array[k].indexOf(".d10") != -1 || L124_stdf_folder_content_array[k].indexOf(".dls") != -1){
						if(L124_stdf_folder_content_array[k].indexOf("_FT") != -1){	//20170913
							cmdStr = "ftp_sigurd_cp_all.csh";

							for (int i = 0; i < L124_File_upload_FT.length; i++){       	
								if(i == 5)
									cmdStr = cmdStr + " " + L124_stdf_folder_content_array[k];
								else
									cmdStr = cmdStr + " " + L124_File_upload_FT[i];	
							}
							javaExecSystemCmd2(cmdStr,3000);
							javaExecSystemCmd2(cmdStr,3000); //Still random lose, double send command. 20161220
						}
					}
					cmdStr = "java_Regular_Expression.csh " + L124_File_upload_FT[3] + " " + L124_stdf_folder_content_array[k] + " " + "/usr/local/home/vol5/stdf_backup/L124/Data_Backup";
					javaExecSystemCmd2(cmdStr,2000);

				}
			}//L124 FT data upload. 20170316-----End
			
			//=================STDF UGO001B110_WebM upload======================start 2015/4/8 by ChiaHui
//			F126_UGO001B110_WebM_STDF_upload = {"192.168.36.72","Verisiliconftp","8aynLF+f","/usr/local/home/vol5/STDF/F126/CP/WEBM/","/STDF/UGO001B110_WebM/",".std.gz"};

			String[] CP_STDF_F126_WebM_folder_content_array;  //確認資料夾內是否有.std檔案

			System.out.println("STDF F126_WebM upload");
			File CP_STDF_F126_WebM_folder_content_array_file = new File("/usr/local/home/vol5/STDF/F126/CP/WEBM/");
			if (CP_STDF_F126_WebM_folder_content_array_file.isDirectory()){//check 檔案內有無.std.gz檔案，有才去執行				            						
				CP_STDF_F126_WebM_folder_content_array = CP_STDF_F126_WebM_folder_content_array_file.list();	
				for (int i = 0; i < CP_STDF_F126_WebM_folder_content_array.length; i++){
					if(CP_STDF_F126_WebM_folder_content_array[i].indexOf(".std.gz") != -1){

						cmdStr = "ftp_sigurd_cp_all.csh";
						for (int k = 0; k < F126_UGO001B110_WebM_STDF_upload.length; k++)        	
							cmdStr = cmdStr + " " + F126_UGO001B110_WebM_STDF_upload[k];						

						javaExecSystemCmd2(cmdStr,10000);
					}
				}
			}	
			//=================STDF JMA001B110_Frizz upload======================start 2015/4/8 by ChiaHui
			//F126_JMA001B110_Frizz_STDF_upload = {"192.168.36.72","Verisiliconftp","8aynLF+f","/usr/local/home/vol5/STDF/F126/CP/FRIZZ/","/STDF/JMA001B110_Frizz/",".std.gz"};
			String[] CP_STDF_F126_Frizz_folder_content_array;  //確認資料夾內是否有.std檔案

			System.out.println("STDF F126_Frizz upload");
			File CP_STDF_F126_Frizz_folder_content_array_file = new File("/usr/local/home/vol5/STDF/F126/CP/FRIZZ/");
			if (CP_STDF_F126_Frizz_folder_content_array_file.isDirectory()){//check 檔案內有無.std.gz檔案，有才去執行				            						
				CP_STDF_F126_Frizz_folder_content_array = CP_STDF_F126_Frizz_folder_content_array_file.list();	
				for (int i = 0; i < CP_STDF_F126_Frizz_folder_content_array.length; i++){
					if(CP_STDF_F126_Frizz_folder_content_array[i].indexOf(".std.gz") != -1){

						cmdStr = "ftp_sigurd_cp_all.csh";
						for (int k = 0; k < F126_JMA001B110_Frizz_STDF_upload.length; k++)        	
							cmdStr = cmdStr + " " + F126_JMA001B110_Frizz_STDF_upload[k];						

						javaExecSystemCmd2(cmdStr,10000);
					}
				}
			}

			//=================L022 L129 L449 STDF upload to OP+======================start	
			AZA10176A_Upload_RunEver = false;
			BHD10238_Upload_RunEver = false;
			AHD10289_Upload_RunEver = false;
			if (MTK_L022_STDF_file.isDirectory()){

				L022_STDF_device_array = MTK_L022_STDF_file.list();
				System.out.println("L022 outer loop");
				for (int L022_STDF_device_array_counter = 0; L022_STDF_device_array_counter < L022_STDF_device_array.length; L022_STDF_device_array_counter++){
					System.out.println("L022 device:" + L022_STDF_device_array[L022_STDF_device_array_counter]);

					File CP_L022_MTK_stdf_folder_content_array_file = new File("/usr/local/home/vol5/STDF/L022/CP/" + L022_STDF_device_array[L022_STDF_device_array_counter]);
					if (CP_L022_MTK_stdf_folder_content_array_file.isDirectory()){//check folder's std file			            						

						CP_L022_MTK_stdf_folder_content_array = CP_L022_MTK_stdf_folder_content_array_file.list();

						if (CP_L022_MTK_stdf_folder_content_array.length != 0){

							if (CP_L022_MTK_stdf_folder_content_array.length > 15){ //Change 1000 to 500 by Cola. 20161108
								forloopNO = 15;
							}else{
								forloopNO = CP_L022_MTK_stdf_folder_content_array.length;
							}
							boolean createDirFlag = false;
							for (int k = 0; k < forloopNO; k++){
								check_CP_L022_MTK_stdf_content_flag = false;
								if(CP_L022_MTK_stdf_folder_content_array[k].indexOf(".std.gz") != -1)
									check_CP_L022_MTK_stdf_content_flag = true;

								if (check_CP_L022_MTK_stdf_content_flag == true){				
									if(!createDirFlag){	//20170327
										createDirFlag = true;
										cmdStr = "mkdir " + L022_STDF_device_array[L022_STDF_device_array_counter] + "/backup";
										javaExecSystemCmd2(cmdStr,1000);
										cmdStr = "chmod 777 " + "/usr/local/home/vol5/STDF/L022/CP/" + L022_STDF_device_array[L022_STDF_device_array_counter] + "/backup";
										javaExecSystemCmd2(cmdStr,1000);
									}
									if(CP_L022_MTK_stdf_folder_content_array[k].indexOf("-CORR") == -1){ //don't send CORR data. 20170327
									/*	Cancel Send to OT+ , by Tango upload. 20170621 
									   cmdStr = "AZA10176A_ftp_sigurd_cp_all.csh";	
										for (int i = 0; i < 5; i++){   	
											if (i != 3){
												cmdStr = cmdStr + " " + L022_MTK_STDF_upload[i]
											}else{
												cmdStr = cmdStr + " " + "/usr/local/home/vol5/STDF/L022/CP/" + L022_STDF_device_array[L022_STDF_device_array_counter] + "/";
											}
										}											

										cmdStr = cmdStr + " " + CP_L022_MTK_stdf_folder_content_array[k] ;
										javaExecSystemCmd2(cmdStr,3000);
									*/
										//==============================================L022 upload to tango. 20170327

										cmdStr = "AZA10176A_ftp_sigurd_cp_all.csh";
										for (int i = 0; i < 5; i++){   	
											if (i != 3){
												cmdStr = cmdStr + " " + L022_MTK_STDF_upload_tango[i];
											}else{
												cmdStr = cmdStr + " " + "/usr/local/home/vol5/STDF/L022/CP/" + L022_STDF_device_array[L022_STDF_device_array_counter] + "/";
											}
										}											

										cmdStr = cmdStr + " " + CP_L022_MTK_stdf_folder_content_array[k] ;
										javaExecSystemCmd2(cmdStr,30000);

										//==============================================

										if(L022_STDF_device_array[L022_STDF_device_array_counter].equals("AZA10176A"))
											AZA10176A_Upload(CP_L022_MTK_stdf_folder_content_array[k]);
										else if(L022_STDF_device_array[L022_STDF_device_array_counter].equals("10238"))
											BHD10238_Upload(CP_L022_MTK_stdf_folder_content_array[k]);
										else if(L022_STDF_device_array[L022_STDF_device_array_counter].equals("10289"))
											AHD10289_Upload(CP_L022_MTK_stdf_folder_content_array[k]);
//										else if(L022_STDF_device_array[L022_STDF_device_array_counter].equals("10057"))
//											MT10057_Upload(CP_L022_MTK_stdf_folder_content_array[k]);
//										else if(L022_STDF_device_array[L022_STDF_device_array_counter].equals("10159"))
//											AN10159_Upload(CP_L022_MTK_stdf_folder_content_array[k]);
//										else if(L022_STDF_device_array[L022_STDF_device_array_counter].equals("BHH67208"))
//											BHH67208_Upload(CP_L022_MTK_stdf_folder_content_array[k]);
									}
									cmdStr = "AZA10176A_java_Regular_Expression.csh " + "/usr/local/home/vol5/STDF/L022/CP/" + L022_STDF_device_array[L022_STDF_device_array_counter] + "/ " +
											CP_L022_MTK_stdf_folder_content_array[k] + " " + "/usr/local/home/vol5/STDF/L022/CP/" + L022_STDF_device_array[L022_STDF_device_array_counter] + "/backup/";
									javaExecSystemCmd2(cmdStr,3000); 


								}
							}
						}else{
							System.out.println("NO file......waitting......");
						}
					}
				}
			}

			if (MTK_L129_STDF_file.isDirectory()){

				L129_STDF_device_array = MTK_L129_STDF_file.list();
				System.out.println("L129 outer loop");
				for (int L129_STDF_device_array_counter = 0; L129_STDF_device_array_counter < L129_STDF_device_array.length; L129_STDF_device_array_counter++){
					System.out.println("L129 device:" + L129_STDF_device_array[L129_STDF_device_array_counter]);

					File CP_L129_MTK_stdf_folder_content_array_file = new File("/usr/local/home/vol5/STDF/L129/CP/" + L129_STDF_device_array[L129_STDF_device_array_counter]);
					if (CP_L129_MTK_stdf_folder_content_array_file.isDirectory()){//check folder's std file			            						

						CP_L129_MTK_stdf_folder_content_array = CP_L129_MTK_stdf_folder_content_array_file.list();

						if (CP_L129_MTK_stdf_folder_content_array.length != 0){

							if (CP_L129_MTK_stdf_folder_content_array.length > 15){
								forloopNO = 15;
							}else{
								forloopNO = CP_L129_MTK_stdf_folder_content_array.length;
							}
							boolean createDirFlag = false;
							for (int k = 0; k < forloopNO; k++){
								check_CP_L129_MTK_stdf_content_flag = false;
								if(CP_L129_MTK_stdf_folder_content_array[k].indexOf(".std.gz") != -1)
									check_CP_L129_MTK_stdf_content_flag = true;

								if (check_CP_L129_MTK_stdf_content_flag == true){	
									if(!createDirFlag){	//20170327
										createDirFlag = true;
										cmdStr = "mkdir " + L129_STDF_device_array[L129_STDF_device_array_counter] + "/backup";
										javaExecSystemCmd2(cmdStr,1000);
										cmdStr = "chmod 777 " + "/usr/local/home/vol5/STDF/L129/CP/" + L129_STDF_device_array[L129_STDF_device_array_counter] + "/backup";
										javaExecSystemCmd2(cmdStr,1000);
									}
									if(CP_L129_MTK_stdf_folder_content_array[k].indexOf("-CORR") == -1){ //don't send CORR data. 20170306						
									/* Cancel Send to OT+ , by Tango upload. 20170621 
										cmdStr = "AZA10176A_ftp_sigurd_cp_all.csh";
										for (int i = 0; i < 5; i++){   	
											if (i != 3){
												cmdStr = cmdStr + " " + L129_MTK_STDF_upload[i];
											}else{
												cmdStr = cmdStr + " " + "/usr/local/home/vol5/STDF/L129/CP/" + L129_STDF_device_array[L129_STDF_device_array_counter] + "/";
											}
										}											

										cmdStr = cmdStr + " " + CP_L129_MTK_stdf_folder_content_array[k] ;
										javaExecSystemCmd2(cmdStr,3000);
									*/
										//==============================================L129 upload to tango
										cmdStr = "AZA10176A_ftp_sigurd_cp_all.csh";
										for (int i = 0; i < 5; i++){   	
											if (i != 3){
												cmdStr = cmdStr + " " + L129_MTK_STDF_upload_tango[i];
											}else{
												cmdStr = cmdStr + " " + "/usr/local/home/vol5/STDF/L129/CP/" + L129_STDF_device_array[L129_STDF_device_array_counter] + "/";
											}
										}											

										cmdStr = cmdStr + " " + CP_L129_MTK_stdf_folder_content_array[k] ;
										javaExecSystemCmd2(cmdStr,30000); //change to 3000 to 30000 by Cola. 20161108
										//==============================================									
									}
									cmdStr = "AZA10176A_java_Regular_Expression.csh " + "/usr/local/home/vol5/STDF/L129/CP/" + L129_STDF_device_array[L129_STDF_device_array_counter] + "/ " +
											CP_L129_MTK_stdf_folder_content_array[k] + " " + "/usr/local/home/vol5/STDF/L129/CP/" + L129_STDF_device_array[L129_STDF_device_array_counter] + "/backup/";
									javaExecSystemCmd2(cmdStr,3000); 
									
									
								}	
							}
						}else{
							System.out.println("NO file......waitting......");
						}
					}
				}
			}
			
			if (MTK_L449_STDF_file.isDirectory()){  // new add L449 upload STDF to OT 20171020

				L449_STDF_device_array = MTK_L449_STDF_file.list();
				System.out.println("L449 outer loop");
				for (int L449_STDF_device_array_counter = 0; L449_STDF_device_array_counter < L449_STDF_device_array.length; L449_STDF_device_array_counter++){
					System.out.println("L449 device:" + L449_STDF_device_array[L449_STDF_device_array_counter]);

					File CP_L449_MTK_stdf_folder_content_array_file = new File("/usr/local/home/vol5/STDF/L449/CP/" + L449_STDF_device_array[L449_STDF_device_array_counter]);
					if (CP_L449_MTK_stdf_folder_content_array_file.isDirectory()){//check folder's std file			            						

						CP_L449_MTK_stdf_folder_content_array = CP_L449_MTK_stdf_folder_content_array_file.list();

						if (CP_L449_MTK_stdf_folder_content_array.length != 0){

							if (CP_L449_MTK_stdf_folder_content_array.length > 15){
								forloopNO = 15;
							}else{
								forloopNO = CP_L449_MTK_stdf_folder_content_array.length;
							}
							boolean createDirFlag = false;
							for (int k = 0; k < forloopNO; k++){
								check_CP_L449_MTK_stdf_content_flag = false;
								if(CP_L449_MTK_stdf_folder_content_array[k].indexOf(".std.gz") != -1)
									check_CP_L449_MTK_stdf_content_flag = true;

								if (check_CP_L449_MTK_stdf_content_flag == true){	
									if(!createDirFlag){	//20170327
										createDirFlag = true;
										cmdStr = "mkdir " + L449_STDF_device_array[L449_STDF_device_array_counter] + "/backup";
										javaExecSystemCmd2(cmdStr,1000);
										cmdStr = "chmod 777 " + "/usr/local/home/vol5/STDF/L449/CP/" + L449_STDF_device_array[L449_STDF_device_array_counter] + "/backup";
										javaExecSystemCmd2(cmdStr,1000);
									}
									if(CP_L449_MTK_stdf_folder_content_array[k].indexOf("-CORR") == -1){ //don't send CORR data. 20170306						
									/* Cancel Send to OT+ , by Tango upload. 20170621 
										cmdStr = "AZA10176A_ftp_sigurd_cp_all.csh";
										for (int i = 0; i < 5; i++){   	
											if (i != 3){
												cmdStr = cmdStr + " " + L449_MTK_STDF_upload[i];
											}else{
												cmdStr = cmdStr + " " + "/usr/local/home/vol5/STDF/L449/CP/" + L449_STDF_device_array[L449_STDF_device_array_counter] + "/";
											}
										}											

										cmdStr = cmdStr + " " + CP_L449_MTK_stdf_folder_content_array[k] ;
										javaExecSystemCmd2(cmdStr,3000);
									*/
										//==============================================L449 upload to tango
										cmdStr = "AZA10176A_ftp_sigurd_cp_all.csh";
										for (int i = 0; i < 5; i++){   	
											if (i != 3){
												cmdStr = cmdStr + " " + L449_MTK_STDF_upload_tango[i];
											}else{
												cmdStr = cmdStr + " " + "/usr/local/home/vol5/STDF/L449/CP/" + L449_STDF_device_array[L449_STDF_device_array_counter] + "/";
											}
										}											

										cmdStr = cmdStr + " " + CP_L449_MTK_stdf_folder_content_array[k] ;
										javaExecSystemCmd2(cmdStr,30000); //change to 3000 to 30000 by Cola. 20161108
										//==============================================									
									}
									cmdStr = "AZA10176A_java_Regular_Expression.csh " + "/usr/local/home/vol5/STDF/L449/CP/" + L449_STDF_device_array[L449_STDF_device_array_counter] + "/ " +
											CP_L449_MTK_stdf_folder_content_array[k] + " " + "/usr/local/home/vol5/STDF/L449/CP/" + L449_STDF_device_array[L449_STDF_device_array_counter] + "/backup/";
									javaExecSystemCmd2(cmdStr,3000); 
									
									
								}	
							}
						}else{
							System.out.println("NO file......waitting......");
						}
					}
				}
			}				
			//=================L022 L129 L449 STDF upload to OP+======================end						 

			//=================D10/SD10 CP STDF upload======================start
			//可能會有CP STDF解晰上傳之後搬去備份，可是卻導致實際上要傳給客戶的那份沒傳!!! 全面切換後可解決  11月8日
			if (STDF_file.isDirectory()){
				STDF_cusNO_array = STDF_file.list();
				System.out.println("outer loop");
				for (int i = 0; i < STDF_cusNO_array.length; i++)
				{
					System.out.println("Upload CP STDF: " + STDF_cusNO_array[i]);
					//Add L124 by Cola. 20161108
					if (!STDF_cusNO_array[i].equals("L124")&&!STDF_cusNO_array[i].equals("L022")&&!STDF_cusNO_array[i].equals("L129")&&!STDF_cusNO_array[i].equals("L389")&&!STDF_cusNO_array[i].equals("L320")) {
						innitial_path_add_cusNO_STDF = innitial_path_STDF + STDF_cusNO_array[i] + "/CP";
						File STDF_file2 = new File(innitial_path_add_cusNO_STDF);

						if (STDF_file2.isDirectory()){
							STDF_device_array = STDF_file2.list();
//							System.out.println("inner loop");   
							for (int j = 0; j < STDF_device_array.length; j++){
								System.out.println("Upload " + STDF_cusNO_array[i] + " CP STDF for Device: "+ STDF_device_array[j]);	
								final_path = innitial_path_add_cusNO_STDF + "/" + STDF_device_array[j];

								File CP_stdf_folder_content_array_file = new File(final_path);
								if (CP_stdf_folder_content_array_file.isDirectory()){//check 檔案內有無.std檔案，有才去執行				            						
									CP_stdf_folder_content_array = CP_stdf_folder_content_array_file.list();
									
									//F154_CP_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/STDF/F154/","F154/cp/stdf",".std.gz"};
									if(STDF_cusNO_array[i].equals("F154")){	//20170320
										for (int k = 0; k < CP_stdf_folder_content_array.length; k++){
											if(CP_stdf_folder_content_array[k].indexOf(".std.gz") != -1){
												System.out.println("F154 STDF upload to 192.168.1.70 !!");
												cmdStr = "ftp_sigurd_cp_all.csh 192.168.1.70 sghpe2 sgh555pe2 " + final_path + " F154/cp/stdf .std.gz";
												javaExecSystemCmd2(cmdStr,10000);
												break;

											}
										}
									}
									
									for (int k = 0; k < CP_stdf_folder_content_array.length; k++){
										
										if(CP_stdf_folder_content_array[k].indexOf(".std.gz") != -1)
											check_CP_stdf_content_flag = true;

										if (check_CP_stdf_content_flag == true){
											if(STDF_cusNO_array[i].equals("F089")) //F089 STDF send to L416 Tango testerdata path by Cola. 20160920
												STDF_cusNO_array[i] = "L416";
											cmdStr = "ftp_sigurd_cp_STDF_upload.csh " + final_path + " " + STDF_cusNO_array[i];
											javaExecSystemCmd2(cmdStr,30000);
											check_CP_stdf_content_flag = false;
											break;
										}
										
									}
									if(STDF_cusNO_array[i].equals("F186")){  //Upload F186 InfoFile to 192.168.1.76 for CP by Cola. 20160707-----Start
										for (int k = 0; k < CP_stdf_folder_content_array.length; k++){
											if(CP_stdf_folder_content_array[k].indexOf("I_") != -1){  
												System.out.println("Upload " + STDF_cusNO_array[i] + " CP InfoFile");
												cmdStr = "java_Regular_Expression_command4.csh " + "cp " + final_path + "/ " + CP_stdf_folder_content_array[k] + " " + final_path + "/backup";
												javaExecSystemCmd2(cmdStr,3000);

//												String[] AZA10176A_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/STDF/L022/CP/AZA10176A/","/L022_GLOBAL/From_Sigurd/STDF_data",".std.gz"};
												cmdStr = "ftp_sigurd_cp_all.csh 192.168.1.76 lantiq_sg vkrXq9_D " + final_path + " " + "/F186/STDF/VIA_CP " + CP_stdf_folder_content_array[k];
												javaExecSystemCmd2(cmdStr, 3000);                       

												cmdStr = "java_Regular_Expression_command3.csh " + "rm -f " + final_path + "/ " + CP_stdf_folder_content_array[k] + " " + "*";
												javaExecSystemCmd2(cmdStr,3000);

											}
										}
									}  //Upload F186 InfoFile to 192.168.1.76 for CP by Cola. 20160707-----End
					
								}
							}            
						}
					}  	               
				}
			}
			else
			{
				System.out.println("Error 目錄錯誤");
			}

			//=================D10/SD10 FT STDF upload======================start
			if (STDF_file_FT.isDirectory()){
				STDF_cusNO_array_FT  = STDF_file_FT.list();
				System.out.println("outer loop");
				for (int i = 0; i < STDF_cusNO_array_FT .length; i++)
				{
//							            		if (!STDF_cusNO_array_FT[i].equals("L022")&&!STDF_cusNO_array_FT[i].equals("L129")&&!STDF_cusNO_array_FT[i].equals("L389")&&!STDF_cusNO_array_FT[i].equals("F128")&&!STDF_cusNO_array_FT[i].equals("L320")){

					cmdStr = "mkdir " + "/usr/local/home/vol5/stdf_backup/" + STDF_cusNO_array_FT[i];
					javaExecSystemCmd2(cmdStr,500);			        

					File FT_stdf_folder_content_array_file = new File("/usr/local/home/vol4/D10/prod/autoResult/summary/autosend/" + STDF_cusNO_array_FT[i]);
					if (FT_stdf_folder_content_array_file.isDirectory()){//check 檔案內有無.std檔案，有才去執行				            						
						FT_stdf_folder_content_array = FT_stdf_folder_content_array_file.list();	
						for (int k = 0; k < FT_stdf_folder_content_array.length; k++){
							if(FT_stdf_folder_content_array[k].indexOf(".std.gz") != -1){

								cmdStr = "java_Regular_Expression_command4.csh " + "cp /usr/local/home/vol4/D10/prod/autoResult/summary/autosend/" + STDF_cusNO_array_FT[i] + "/ " + FT_stdf_folder_content_array[k] + " " + "/usr/local/home/vol5/stdf_backup/" + STDF_cusNO_array_FT[i];
								javaExecSystemCmd2(cmdStr,5000);
								File FT_STDF_backup_path_fileName = new File("/usr/local/home/vol5/stdf_backup/" + STDF_cusNO_array_FT[i] + "/" + FT_stdf_folder_content_array[k]);
								int timer = 0;
								while(!FT_STDF_backup_path_fileName.isFile()){
									Thread.sleep(3000);// unit: ms
									timer = timer + 3;
									System.out.println(STDF_cusNO_array_FT[i] + "   copying to stdf_backup......" + timer);
									if (timer >= 300){
										System.out.println("stop copying to stdf_backup......" + timer);
										break;
									}
								}
								System.out.println("Upload FT STDF: " + STDF_cusNO_array_FT[i]);
								//									System.out.println(cmdStr);
								innitial_path_add_cusNO_STDF_FT = innitial_path_STDF_FT  + STDF_cusNO_array_FT[i];

								//									System.out.println("inner loop");   
								if(!STDF_cusNO_array_FT[i].equals("F152")){ //20170720
									cmdStr = "ftp_sigurd_FT_STDF_upload.csh " + innitial_path_add_cusNO_STDF_FT + " " + STDF_cusNO_array_FT[i] + " " + FT_stdf_folder_content_array[k];
									System.out.println(cmdStr);
									javaExecSystemCmd2(cmdStr,50000);                       
								}
								cmdStr = "java_Regular_Expression_command3.csh " + "rm -f " + "/usr/local/home/vol4/D10/prod/autoResult/summary/autosend/"  + STDF_cusNO_array_FT[i] + "/ " + FT_stdf_folder_content_array[k] + " " + "*";
								javaExecSystemCmd2(cmdStr,3000);
								System.out.println(cmdStr);

							}
							if(STDF_cusNO_array_FT[i].equals("F186")){  //Upload F186 InfoFile to 192.168.1.5 for FT by Cola. 20160707
								if(FT_stdf_folder_content_array[k].indexOf("I_") != -1){  
									System.out.println("Upload " + STDF_cusNO_array_FT[i] + " FT InfoFile");
									cmdStr = "java_Regular_Expression_command4.csh " + "cp /usr/local/home/vol4/D10/prod/autoResult/summary/autosend/" + STDF_cusNO_array_FT[i] + "/ " + FT_stdf_folder_content_array[k] + " " + "/usr/local/home/vol5/stdf_backup/" + STDF_cusNO_array_FT[i];
									javaExecSystemCmd2(cmdStr,3000);

									innitial_path_add_cusNO_STDF_FT = innitial_path_STDF_FT  + STDF_cusNO_array_FT[i];

									//								String[] AZA10176A_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/STDF/L022/CP/AZA10176A/","/L022_GLOBAL/From_Sigurd/STDF_data",".std.gz"};
									cmdStr = "ftp_sigurd_cp_all.csh 192.168.1.5 asipe2 asi555pe2 " + innitial_path_add_cusNO_STDF_FT + " " + "/customer/F186/FT/INFO " + FT_stdf_folder_content_array[k];
									javaExecSystemCmd2(cmdStr, 3000);                       

									cmdStr = "java_Regular_Expression_command3.csh " + "rm -f " + "/usr/local/home/vol4/D10/prod/autoResult/summary/autosend/"  + STDF_cusNO_array_FT[i] + "/ " + FT_stdf_folder_content_array[k] + " " + "*";
									javaExecSystemCmd2(cmdStr,3000);

								}
							}
						}
					}
				}
			}
			else
			{
				System.out.println("Error 目錄錯誤");
			}


			//=================F071 STDF upload========================start		        
			cmdStr = "ftp_sigurd_cp_all.csh";
			for (int k = 0; k < F071_STDF_upload1.length; k++)        	
				cmdStr = cmdStr + " " + F071_STDF_upload1[k];

			javaExecSystemCmd2(cmdStr,10000);
			cmdStr = "java_Regular_Expression.csh " + F071_STDF_upload1[3] + " " + F071_STDF_upload1[5] + " " + "/usr/local/home/vol5/stdf_backup/F071/MXL584";
			javaExecSystemCmd2(cmdStr,3000);  


			cmdStr = "ftp_sigurd_cp_all.csh";
			for (int k = 0; k < F071_STDF_upload2.length; k++)        	
				cmdStr = cmdStr + " " + F071_STDF_upload2[k];

			javaExecSystemCmd2(cmdStr,10000);
			cmdStr = "java_Regular_Expression.csh " + F071_STDF_upload2[3] + " " + F071_STDF_upload2[5] + " " + "/usr/local/home/vol5/stdf_backup/F071/GINJO_V1";
			javaExecSystemCmd2(cmdStr,3000); 


			cmdStr = "ftp_sigurd_cp_all.csh";
			for (int k = 0; k < F071_STDF_upload3.length; k++)        	
				cmdStr = cmdStr + " " + F071_STDF_upload3[k];

			javaExecSystemCmd2(cmdStr,10000);
			cmdStr = "java_Regular_Expression.csh " + F071_STDF_upload3[3] + " " + F071_STDF_upload3[5] + " " + "/usr/local/home/vol5/stdf_backup/F071/Minotaur_V9";
			javaExecSystemCmd2(cmdStr,3000); 						


			cmdStr = "ftp_sigurd_cp_all.csh";
			for (int k = 0; k < F071_STDF_upload4.length; k++)        	
				cmdStr = cmdStr + " " + F071_STDF_upload4[k];

			javaExecSystemCmd2(cmdStr,10000);
			cmdStr = "java_Regular_Expression.csh " + F071_STDF_upload4[3] + " " + F071_STDF_upload4[5] + " " + "/usr/local/home/vol5/stdf_backup/F071/MxL267";
			javaExecSystemCmd2(cmdStr,3000); 						


			cmdStr = "ftp_sigurd_cp_all.csh";
			for (int k = 0; k < F071_STDF_upload5.length; k++)        	
				cmdStr = cmdStr + " " + F071_STDF_upload5[k];

			javaExecSystemCmd2(cmdStr,10000);
			cmdStr = "java_Regular_Expression.csh " + F071_STDF_upload5[3] + " " + F071_STDF_upload5[5] + " " + "/usr/local/home/vol5/stdf_backup/F071/MXL581";
			javaExecSystemCmd2(cmdStr,3000); 


			cmdStr = "ftp_sigurd_cp_all.csh";
			for (int k = 0; k < F071_STDF_upload6.length; k++)        	
				cmdStr = cmdStr + " " + F071_STDF_upload6[k];

			javaExecSystemCmd2(cmdStr,10000);
			cmdStr = "java_Regular_Expression.csh " + F071_STDF_upload6[3] + " " + F071_STDF_upload6[5] + " " + "/usr/local/home/vol5/stdf_backup/F071/Thor_v2";
			javaExecSystemCmd2(cmdStr,3000); 		

			cmdStr = "ftp_sigurd_cp_all.csh";
			for (int k = 0; k < F071_STDF_upload7.length; k++)        	
				cmdStr = cmdStr + " " + F071_STDF_upload7[k];

			javaExecSystemCmd2(cmdStr,10000);
			cmdStr = "java_Regular_Expression.csh " + F071_STDF_upload7[3] + " " + F071_STDF_upload7[5] + " " + "/usr/local/home/vol5/stdf_backup/F071/MXL586";
			javaExecSystemCmd2(cmdStr,3000);																			

			cmdStr = "ftp_sigurd_cp_all.csh";
			for (int k = 0; k < F071_STDF_upload8.length; k++)        	
				cmdStr = cmdStr + " " + F071_STDF_upload8[k];

			javaExecSystemCmd2(cmdStr,10000);
			cmdStr = "java_Regular_Expression.csh " + F071_STDF_upload8[3] + " " + F071_STDF_upload8[5] + " " + "/usr/local/home/vol5/stdf_backup/F071/MXL267E0/";
			javaExecSystemCmd2(cmdStr,3000);

			//=================L178 CP STDF upload========================start
			/*        	String[] L178_CP_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/RealTimelog/L178/Temp/","/L178/CP/",".gz"};
						cmdStr = "java_Regular_Expression_command.csh "  + "gzip " + L178_CP_upload[3] + " " + ".txt";		        
						javaExecSystemCmd2(cmdStr,5000);

		        cmdStr = "ftp_sigurd_cp_all.csh";
		        for (int k = 0; k < L178_CP_upload.length; k++)        	
		        		cmdStr = cmdStr + " " + L178_CP_upload[k];

		        javaExecSystemCmd2(cmdStr,3000);

						cmdStr = "java_Regular_Expression.csh " + L178_CP_upload[3] + " " + L178_CP_upload[5] + " " + "/usr/local/home/vol5/RealTimelog/L178/CP/";
		        javaExecSystemCmd2(cmdStr,3000);		
			 */		     	
			
			//=================RealTimelog upload======================start
			for(int i = 0; i < UploadRealTimeLog_Device_ForMTK.length; i++){  //by Cola. 2016.06.
				System.out.println("Search Device: " + UploadRealTimeLog_Device_ForMTK[i] + " .....");
				TransferRealTimeLogforHRID(UploadRealTimeLog_Device_ForMTK[i]);
			}			
			//=================RealTimelog upload======================end
/*			
			//=================RealTimelog BM10060 upload======================start
			String[] CP_RealTimelog_BM10060_folder_content_array;  //確認資料夾內是否有.std檔案

			System.out.println("RealTimelog BM10060 upload");
			File CP_RealTimelog_BM10060_folder_content_array_file = new File("/usr/local/home/vol5/RealTimelog/L022/CP/10060/");
			if (CP_RealTimelog_BM10060_folder_content_array_file.isDirectory()){//check 檔案內有無.std檔案，有才去執行				            						
				CP_RealTimelog_BM10060_folder_content_array = CP_RealTimelog_BM10060_folder_content_array_file.list();	
				for (int i = 0; i < CP_RealTimelog_BM10060_folder_content_array.length; i++){
					if(CP_RealTimelog_BM10060_folder_content_array[i].indexOf(".txt.gz") != -1){

						cmdStr = "ftp_sigurd_cp_all.csh";
						for (int k = 0; k < L022_10060_realTimeLog_upload.length; k++)        	
							cmdStr = cmdStr + " " + L022_10060_realTimeLog_upload[k];						

						javaExecSystemCmd2(cmdStr,10000);
						cmdStr = "java_Regular_Expression.csh " + L022_10060_realTimeLog_upload[3] + " " + L022_10060_realTimeLog_upload[5] + " " + "/usr/local/home/vol5/RealTimelog/L129/CP/AM10168/backup";
						javaExecSystemCmd2(cmdStr,10000); 
					}
				}
			}
			//=================RealTimelog AM10168 upload======================start
			String[] CP_RealTimelog_AM10168_folder_content_array;  //確認資料夾內是否有.std檔案

			System.out.println("RealTimelog AM10168 upload");
			File CP_RealTimelog_AM10168_folder_content_array_file = new File("/usr/local/home/vol5/RealTimelog/L129/CP/AM10168/");
			if (CP_RealTimelog_AM10168_folder_content_array_file.isDirectory()){//check 檔案內有無.std檔案，有才去執行				            						
				CP_RealTimelog_AM10168_folder_content_array = CP_RealTimelog_AM10168_folder_content_array_file.list();	
				for (int i = 0; i < CP_RealTimelog_AM10168_folder_content_array.length; i++){
					if(CP_RealTimelog_AM10168_folder_content_array[i].indexOf(".txt.gz") != -1){

						cmdStr = "ftp_sigurd_cp_all.csh";
						for (int k = 0; k < L129_AM10168_realTimeLog_upload.length; k++)        	
							cmdStr = cmdStr + " " + L129_AM10168_realTimeLog_upload[k];						

						javaExecSystemCmd2(cmdStr,10000);
						cmdStr = "java_Regular_Expression.csh " + L129_AM10168_realTimeLog_upload[3] + " " + L129_AM10168_realTimeLog_upload[5] + " " + "/usr/local/home/vol5/RealTimelog/L129/CP/AM10168/backup";
						javaExecSystemCmd2(cmdStr,10000); 
					}
				}
			}
			//=================RealTimelog AJC10168 upload======================start
			String[] CP_RealTimelog_AJC10168_folder_content_array;  //確認資料夾內是否有.std檔案

			System.out.println("RealTimelog AJC10168 upload");
			File CP_RealTimelog_AJC10168_folder_content_array_file = new File("/usr/local/home/vol5/RealTimelog/L129/CP/AJC10168/");
			if (CP_RealTimelog_AJC10168_folder_content_array_file.isDirectory()){//check 檔案內有無.txt.gz檔案，有才去執行				            						
				CP_RealTimelog_AJC10168_folder_content_array = CP_RealTimelog_AJC10168_folder_content_array_file.list();	
				for (int i = 0; i < CP_RealTimelog_AJC10168_folder_content_array.length; i++){
					if(CP_RealTimelog_AJC10168_folder_content_array[i].indexOf(".txt.gz") != -1){

						cmdStr = "ftp_sigurd_cp_all.csh";
						for (int k = 0; k < L129_AJC10168_realTimeLog_upload.length; k++)        	
							cmdStr = cmdStr + " " + L129_AJC10168_realTimeLog_upload[k];						

						javaExecSystemCmd2(cmdStr,10000);
						cmdStr = "java_Regular_Expression.csh " + L129_AJC10168_realTimeLog_upload[3] + " " + L129_AJC10168_realTimeLog_upload[5] + " " + "/usr/local/home/vol5/RealTimelog/L129/CP/AJC10168/backup";
						javaExecSystemCmd2(cmdStr,10000); 
					}
				}
			}	
			//=================RealTimelog AN10363C upload======================start
			//String[] L129_AN10363C_realTimeLog_upload = {"192.168.1.76","sghpe2","sgh555pe2","/usr/local/home/vol5/RealTimelog/L129/CP/AN10363/","/HRID/AN10363C",".txt.gz"};
			String[] CP_RealTimelog_AN10363C_folder_content_array;  //確認資料夾內是否有.std檔案

			System.out.println("RealTimelog AN10363C upload");
			File CP_RealTimelog_AN10363C_folder_content_array_file = new File("/usr/local/home/vol5/RealTimelog/L129/CP/AN10363/");
			if (CP_RealTimelog_AN10363C_folder_content_array_file.isDirectory()){//check 檔案內有無.txt.gz檔案，有才去執行				            						
				CP_RealTimelog_AN10363C_folder_content_array = CP_RealTimelog_AN10363C_folder_content_array_file.list();	
				for (int i = 0; i < CP_RealTimelog_AN10363C_folder_content_array.length; i++){
					if(CP_RealTimelog_AN10363C_folder_content_array[i].indexOf(".txt.gz") != -1){

						cmdStr = "ftp_sigurd_cp_all.csh";
						for (int k = 0; k < L129_AN10363C_realTimeLog_upload.length; k++)        	
							cmdStr = cmdStr + " " + L129_AN10363C_realTimeLog_upload[k];						

						javaExecSystemCmd2(cmdStr,10000);
						cmdStr = "java_Regular_Expression.csh " + L129_AN10363C_realTimeLog_upload[3] + " " + L129_AN10363C_realTimeLog_upload[5] + " " + "/usr/local/home/vol5/RealTimelog/L129/CP/AN10363/backup";
						javaExecSystemCmd2(cmdStr,10000); 
					}
				}
			}
			//=================RealTimelog AE10363C upload======================start 2015/2/4 by ChiaHui
			//String[] L129_AE10363C_realTimeLog_upload = {"192.168.1.76","sghpe2","sgh555pe2","/usr/local/home/vol5/RealTimelog/L129/CP/AE10363/","/HRID/AN10363C",".txt.gz"};
			String[] CP_RealTimelog_AE10363C_folder_content_array;  //確認資料夾內是否有.std檔案

			System.out.println("RealTimelog AE10363C upload");
			File CP_RealTimelog_AE10363C_folder_content_array_file = new File("/usr/local/home/vol5/RealTimelog/L129/CP/AE10363/");
			if (CP_RealTimelog_AE10363C_folder_content_array_file.isDirectory()){//check 檔案內有無.txt.gz檔案，有才去執行				            						
				CP_RealTimelog_AE10363C_folder_content_array = CP_RealTimelog_AE10363C_folder_content_array_file.list();	
				for (int i = 0; i < CP_RealTimelog_AE10363C_folder_content_array.length; i++){
					if(CP_RealTimelog_AE10363C_folder_content_array[i].indexOf(".txt.gz") != -1){

						cmdStr = "ftp_sigurd_cp_all.csh";
						for (int k = 0; k < L129_AE10363C_realTimeLog_upload.length; k++)        	
							cmdStr = cmdStr + " " + L129_AE10363C_realTimeLog_upload[k];						

						javaExecSystemCmd2(cmdStr,10000);
						cmdStr = "java_Regular_Expression.csh " + L129_AE10363C_realTimeLog_upload[3] + " " + L129_AE10363C_realTimeLog_upload[5] + " " + "/usr/local/home/vol5/RealTimelog/L129/CP/AE10363/backup";
						javaExecSystemCmd2(cmdStr,10000); 
					}
				}
			}
			//=================RealTimelog AW10363 upload======================start 2015/2/13 by ChiaHui
			//String[] L129_AW10363_realTimeLog_upload = {"192.168.1.76","sghpe2","sgh555pe2","/usr/local/home/vol5/RealTimelog/L129/CP/AW10363/","/HRID/AW10363",".txt.gz"};
			String[] CP_RealTimelog_AW10363_folder_content_array;  //確認資料夾內是否有.std檔案

			System.out.println("RealTimelog AW10363 upload");
			File CP_RealTimelog_AW10363_folder_content_array_file = new File("/usr/local/home/vol5/RealTimelog/L129/CP/AW10363/");
			if (CP_RealTimelog_AW10363_folder_content_array_file.isDirectory()){//check 檔案內有無.txt.gz檔案，有才去執行				            						
				CP_RealTimelog_AW10363_folder_content_array = CP_RealTimelog_AW10363_folder_content_array_file.list();	
				for (int i = 0; i < CP_RealTimelog_AW10363_folder_content_array.length; i++){
					if(CP_RealTimelog_AW10363_folder_content_array[i].indexOf(".txt.gz") != -1){

						cmdStr = "ftp_sigurd_cp_all.csh";
						for (int k = 0; k < L129_AW10363_realTimeLog_upload.length; k++)        	
							cmdStr = cmdStr + " " + L129_AW10363_realTimeLog_upload[k];						

						javaExecSystemCmd2(cmdStr,10000);
						cmdStr = "java_Regular_Expression.csh " + L129_AW10363_realTimeLog_upload[3] + " " + L129_AW10363_realTimeLog_upload[5] + " " + "/usr/local/home/vol5/RealTimelog/L129/CP/AW10363/backup";
						javaExecSystemCmd2(cmdStr,10000); 
					}
				}
			}						 														 									
			//=================F128 STDF upload======================start
*/
			cmdStr = "java_Regular_Expression_command4.csh " + "cp /usr/local/home/vol4/D10/prod/autoResult/summary/autosend/F128/ " + ".gz " + "/usr/local/home/vol5/stdf_backup/F128/";
			javaExecSystemCmd2(cmdStr,20000);						

			cmdStr = "java_Regular_Expression_command3.csh " + "rm -f " + F128_STDF_upload[3] + " " + "QA " + ".gz";
			javaExecSystemCmd2(cmdStr,10000);

			cmdStr = "ftp_sigurd_cp_all.csh";
			for (int k = 0; k < F128_STDF_upload.length; k++)        	
				cmdStr = cmdStr + " " + F128_STDF_upload[k];

			javaExecSystemCmd2(cmdStr,20000);

			cmdStr = "java_Regular_Expression_command3.csh " + "rm -f " + F128_STDF_upload[3] + " " + "FT " + ".gz";
			javaExecSystemCmd2(cmdStr,1000);
			//=================D10/SD10 CP L010 gzip and upload upload======================start        	        	

			if (L010_upload.isDirectory()){
				L010_device_array = L010_upload.list();
				System.out.println("outer loop");
				for (int i = 0; i < L010_device_array.length; i++){

					check_L010_content_flag = false;
					System.out.println(L010_device_array[i]);
					innitial_path_L010_add_cusNO = innitial_path_L010_upload + L010_device_array[i] + "/data_transfer/";
					System.out.println(innitial_path_L010_add_cusNO);
					File L010_folder_content = new File(innitial_path_L010_add_cusNO);
					if (L010_folder_content.isDirectory()){//check 檔案內有無.map .rep .std等等檔案，有才去執行
						L010_folder_content_array = L010_folder_content.list();	
						for (int k = 0; k < L010_folder_content_array.length; k++)
							if(L010_folder_content_array[k].indexOf(".") != -1)
								check_L010_content_flag = true;

						TaiwanTime = nowdate.format(new java.util.Date());

						if (check_L010_content_flag == true){

							L010_tar_gz_fileName = "asicp" + TaiwanTime;
							cmdStr = "L010_upload_try.csh " + innitial_path_L010_add_cusNO + " " + L010_tar_gz_fileName;
							javaExecSystemCmd2(cmdStr,20000);

							cmdStr = "java_Regular_Expression_command.csh " + "rm" + " " + innitial_path_L010_add_cusNO + " " + ".*";
							javaExecSystemCmd2(cmdStr,3000);	
							check_L010_content_flag = false;
							break;
						}
					}
				}
			}
			else
			{
				System.out.println("Error 目錄錯誤");
			}						
			//=================F152 FT zip upload========================start(待F152 dataCollection.cpp完成後再打開)	        
			//String[] F152_data_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/stdf_backup/F152/","/F152/ProductionData/",".zip"};

			cmdStr = "ftp_sigurd_cp_all.csh";
			for (int k = 0; k < F152_data_upload.length; k++)        	
				cmdStr = cmdStr + " " + F152_data_upload[k];

			javaExecSystemCmd2(cmdStr,3000);
			cmdStr = "java_Regular_Expression.csh " + F152_data_upload[3] + " " + F152_data_upload[5] + " " + "/usr/local/home/vol5/stdf_backup/F152/backup/";
			javaExecSystemCmd2(cmdStr,3000); 
			
			//=================F137 STDF upload======================start
			//F137_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/STDF/F137/STDF_transfer/transfer_tmp/","/F137/data/from_sigurd/STDF/",".std.gz"};

//			File CP_F137_stdf_folder_content_array_file = new File("/usr/local/home/vol5/STDF/F137/STDF_transfer/");
//			if (CP_F137_stdf_folder_content_array_file.isDirectory()){//check 檔案內有無.std檔案，有才去執行				            							
//
//				CP_F137_stdf_folder_content_array = CP_F137_stdf_folder_content_array_file.list();	
//				cmdStr = "java_Regular_Expression.csh " + "/usr/local/home/vol5/STDF/F137/STDF_transfer/" + " " + "corr " + F137_STDF_upload[3] + "backup";
//				javaExecSystemCmd2(cmdStr,3000);								
//
//				cmdStr = "java_Regular_Expression.csh " + "/usr/local/home/vol5/STDF/F137/STDF_transfer/" + " " + F137_STDF_upload[5] + " " + "/usr/local/home/vol5/STDF/F137/STDF_transfer/transfer_tmp/";
//				javaExecSystemCmd2(cmdStr,3000); 
//				
//				for (int k = 0; k < CP_F137_stdf_folder_content_array.length; k++){
//					if(CP_F137_stdf_folder_content_array[k].indexOf(".std.gz") != -1)
//						check_CP_F137_stdf_content_flag = true;
//
//					if (check_CP_F137_stdf_content_flag == true){				            										            						
//
//						if(CP_F137_stdf_folder_content_array[k].indexOf("CP1") != -1) //20170316
//							F137_STDF_upload[4] = "/F137/data/from_sigurd/STDF/STDF_CP1";
//						else if(CP_F137_stdf_folder_content_array[k].indexOf("CP2") != -1)
//							F137_STDF_upload[4] = "/F137/data/from_sigurd/STDF/STDF_CP2";
////						else if(CP_F137_stdf_folder_content_array[k].indexOf("CP2") != -1)
////							F137_STDF_upload[4] = "/F137/data/from_sigurd/STDF/STDF_BP";
//						
//						cmdStr = "ftp_sigurd_cp_all.csh";
//						for (int i = 0; i < F137_STDF_upload.length; i++){
//							if(i == 5)
//								cmdStr = CP_F137_stdf_folder_content_array[k];
//							else
//								cmdStr = cmdStr + " " + F137_STDF_upload[i];
//						}
//
//						javaExecSystemCmd2(cmdStr,5000);
//						cmdStr = "java_Regular_Expression.csh " + F137_STDF_upload[3] + " " + CP_F137_stdf_folder_content_array[k] + " " + "/usr/local/home/vol5/STDF/F137/STDF_transfer/backup/";
//						javaExecSystemCmd2(cmdStr,1000); 
//
//					}
//				}
//			}	
			//=================L285 STDF upload======================start
			//        String[] L285_STDF_upload = {"192.168.1.5","L285","d_SD5u9inz","/usr/local/home/vol4/D10/prod/autoResult/summary/L285/Summary/temp/","/CP/SUMMARY/",".dat.gz"};

			File CP_L285_stdf_folder_content_array_file = new File("/usr/local/home/vol4/D10/prod/autoResult/summary/L285/Summary/");
			if (CP_L285_stdf_folder_content_array_file.isDirectory()){//check 檔案內有無.dat檔案，有才去執行				            							

				CP_L285_stdf_folder_content_array = CP_L285_stdf_folder_content_array_file.list();	
				for (int k = 0; k < CP_L285_stdf_folder_content_array.length; k++){
					if(CP_L285_stdf_folder_content_array[k].indexOf(".dat") != -1)
						check_CP_L285_stdf_content_flag = true;

					if (check_CP_L285_stdf_content_flag == true){				            										            											

						cmdStr = "java_Regular_Expression_command.csh "  + "gzip " + "/usr/local/home/vol4/D10/prod/autoResult/summary/L285/Summary/" + " " + ".dat";	
						javaExecSystemCmd2(cmdStr,3000);

						cmdStr = "java_Regular_Expression.csh " + "/usr/local/home/vol4/D10/prod/autoResult/summary/L285/Summary/" + " " + ".dat.gz" + " " + "/usr/local/home/vol4/D10/prod/autoResult/summary/L285/Summary/temp/";
						javaExecSystemCmd2(cmdStr,3000); 

						cmdStr = "ftp_sigurd_cp_all.csh";
						for (int i = 0; i < L285_STDF_upload.length; i++)        	
							cmdStr = cmdStr + " " + L285_STDF_upload[i];						

						javaExecSystemCmd2(cmdStr,5000);
						cmdStr = "java_Regular_Expression.csh " + L285_STDF_upload[3] + " " + L285_STDF_upload[5] + " " + "/usr/local/home/vol5/stdf_backup/L285/";
						javaExecSystemCmd2(cmdStr,5000); 
						break;
					}
				}
			}								

			//=================F152 FT zip upload========================	        
			//String[] L405_data_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/MAP/L405/7376/dat/","/L405/","GLI7376G.dat"};

			cmdStr = "ftp_sigurd_cp_all.csh";
			for (int k = 0; k < L405_data_upload.length; k++)        	
				cmdStr = cmdStr + " " + L405_data_upload[k];

			javaExecSystemCmd2(cmdStr,3000);
			cmdStr = "java_Regular_Expression.csh " + L405_data_upload[3] + " " + L405_data_upload[5] + " " + "/usr/local/home/vol5/MAP/L405/7376/dat/L405_dat_backup/";
			javaExecSystemCmd2(cmdStr,3000); 																							        
			//=================STDF L416 & F089 FT upload======================start 2015/11/24 by ChiaHui
			//String[] L416_FT_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/stdf_backup/L416/","/L416/FT/",".std.gz"};
			//String[] F089_FT_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/stdf_backup/F089/","/L416/FT/",".std.gz"};

			String[] L416_FT_STDF_folder_content_array;  //確認資料夾內是否有.std檔案

			System.out.println("STDF FT L416 upload");
			File L416_FT_STDF_folder_content_array_file = new File("/usr/local/home/vol5/stdf_backup/L416/");
			if (L416_FT_STDF_folder_content_array_file.isDirectory()){//check 檔案內有無.std.gz檔案，有才去執行				            						
				L416_FT_STDF_folder_content_array = L416_FT_STDF_folder_content_array_file.list();	
				for (int i = 0; i < L416_FT_STDF_folder_content_array.length; i++){
					if(L416_FT_STDF_folder_content_array[i].indexOf(".std.gz") != -1){

						cmdStr = "ftp_sigurd_cp_all.csh";
						for (int k = 0; k < L416_FT_STDF_upload.length; k++)        	
							cmdStr = cmdStr + " " + L416_FT_STDF_upload[k];						

						javaExecSystemCmd2(cmdStr,10000);
					}
				}
			}
			cmdStr = "java_Regular_Expression.csh " + L416_FT_STDF_upload[3] + " " + L416_FT_STDF_upload[5] + " " + "/usr/local/home/vol5/stdf_backup/L416/backup/";
			javaExecSystemCmd2(cmdStr,3000);

			// F089
			String[] F089_FT_STDF_folder_content_array;  //確認資料夾內是否有.std檔案

			System.out.println("STDF FT F089 upload");
			File F089_FT_STDF_folder_content_array_file = new File("/usr/local/home/vol5/stdf_backup/F089/");
			if (F089_FT_STDF_folder_content_array_file.isDirectory()){//check 檔案內有無.std.gz檔案，有才去執行				            						
				F089_FT_STDF_folder_content_array = F089_FT_STDF_folder_content_array_file.list();	
				for (int i = 0; i < F089_FT_STDF_folder_content_array.length; i++){
					if(F089_FT_STDF_folder_content_array[i].indexOf(".std.gz") != -1){

						cmdStr = "ftp_sigurd_cp_all.csh";
						for (int k = 0; k < F089_FT_STDF_upload.length; k++)        	
							cmdStr = cmdStr + " " + F089_FT_STDF_upload[k];						

						javaExecSystemCmd2(cmdStr,10000);
					}
				}
			}
			cmdStr = "java_Regular_Expression.csh " + F089_FT_STDF_upload[3] + " " + F089_FT_STDF_upload[5] + " " + "/usr/local/home/vol5/stdf_backup/F089/backup/";
			javaExecSystemCmd2(cmdStr,3000);
/*Remark 20170320-----Start
			//=================STDF F089 upload======================start 2015/11/24 by ChiaHui
			//String[] L416_CP_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/STDF/L416/","/L416/CP/",".std.gz"};
			//String[] F089_CP_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/STDF/F089/","/L416/CP/",".std.gz"};

			String[] L416_CP_STDF_folder_content_array;  //確認資料夾內是否有.std檔案

			System.out.println("STDF CP L416 upload");
			File L416_CP_STDF_folder_content_array_file = new File("/usr/local/home/vol5/STDF/L416/");
			if (L416_CP_STDF_folder_content_array_file.isDirectory()){//check 檔案內有無.std.gz檔案，有才去執行				            						
				L416_CP_STDF_folder_content_array = L416_CP_STDF_folder_content_array_file.list();	
				for (int i = 0; i < L416_CP_STDF_folder_content_array.length; i++){
					if(L416_CP_STDF_folder_content_array[i].indexOf(".std.gz") != -1){

						cmdStr = "ftp_sigurd_cp_all.csh";
						for (int k = 0; k < L416_CP_STDF_upload.length; k++)        	
							cmdStr = cmdStr + " " + L416_CP_STDF_upload[k];						

						javaExecSystemCmd2(cmdStr,10000);
					}
				}
			}
			cmdStr = "java_Regular_Expression.csh " + L416_CP_STDF_upload[3] + " " + L416_CP_STDF_upload[5] + " " + "/usr/local/home/vol5/STDF/L416/backup/";
			javaExecSystemCmd2(cmdStr,3000);

			// F089
			String[] F089_CP_STDF_folder_content_array;  //確認資料夾內是否有.std檔案

			System.out.println("STDF CP F089 upload");
			File F089_CP_STDF_folder_content_array_file = new File("/usr/local/home/vol5/STDF/F089/");
			if (F089_CP_STDF_folder_content_array_file.isDirectory()){//check 檔案內有無.std.gz檔案，有才去執行				            						
				F089_CP_STDF_folder_content_array = F089_CP_STDF_folder_content_array_file.list();	
				for (int i = 0; i < F089_CP_STDF_folder_content_array.length; i++){
					if(F089_CP_STDF_folder_content_array[i].indexOf(".std.gz") != -1){

						cmdStr = "ftp_sigurd_cp_all.csh";
						for (int k = 0; k < F089_CP_STDF_upload.length; k++)        	
							cmdStr = cmdStr + " " + F089_CP_STDF_upload[k];						

						javaExecSystemCmd2(cmdStr,10000);
					}
				}
			}
			cmdStr = "java_Regular_Expression.csh " + F089_CP_STDF_upload[3] + " " + F089_CP_STDF_upload[5] + " " + "/usr/local/home/vol5/STDF/F089/backup/";
			javaExecSystemCmd2(cmdStr,3000);
Remark 20170320-----End */
			//=================L124 STDF upload======================start   old function
			//        String[] L124_D10File_upload = {"192.168.1.5","asipe2","asi555pe2","/usr/local/home/vol5/STDF/L124/Data_transfor/","/customer/L124/Datalog/",".d10"};
			//        String[] L124_dlsFile_upload = {"192.168.1.5","asipe2","asi555pe2","/usr/local/home/vol5/STDF/L124/Data_transfor/","/customer/L124/Datalog/",".dls"}; 
/*
			File CP_L124_stdf_folder_content_array_file = new File("/usr/local/home/vol5/STDF/L124/Data_transfor/");
			if (CP_L124_stdf_folder_content_array_file.isDirectory()){//check 檔案內有無.dat檔案，有才去執行

				CP_L124_stdf_folder_content_array = CP_L124_stdf_folder_content_array_file.list();	
				for (int k = 0; k < CP_L124_stdf_folder_content_array.length; k++){
					if(CP_L124_stdf_folder_content_array[k].indexOf(".d10") != -1 || CP_L124_stdf_folder_content_array[k].indexOf(".dls") != -1)
						check_CP_L124_stdf_content_flag = true;

					if (check_CP_L124_stdf_content_flag == true){

						cmdStr = "ftp_sigurd_cp_all.csh";

						for (int i = 0; i < L124_D10File_upload.length; i++)        	
							cmdStr = cmdStr + " " + L124_D10File_upload[i];						
						javaExecSystemCmd2(cmdStr,5000);

						cmdStr = "ftp_sigurd_cp_all.csh";

						for (int i = 0; i < L124_dlsFile_upload.length; i++)        	
							cmdStr = cmdStr + " " + L124_dlsFile_upload[i];						
						javaExecSystemCmd2(cmdStr,5000);								        

						cmdStr = "java_Regular_Expression.csh " + L124_D10File_upload[3] + " " + L124_D10File_upload[5] + " " + "/usr/local/home/vol5/STDF/L124/Data_Backup/";
						javaExecSystemCmd2(cmdStr,5000);

						cmdStr = "java_Regular_Expression.csh " + L124_dlsFile_upload[3] + " " + L124_dlsFile_upload[5] + " " + "/usr/local/home/vol5/STDF/L124/Data_Backup/";
						javaExecSystemCmd2(cmdStr,5000);

						break;
					}
				}
			}     */           						
			/*=================L178 FT STDF upload========================start
						cmdStr = "java_Regular_Expression_command.csh "  + "gzip " + L178_FT_upload[3] + " " + ".txt";	
						javaExecSystemCmd2(cmdStr,3000);

		        cmdStr = "ftp_sigurd_cp_all.csh";
		        for (int k = 0; k < L178_FT_upload.length; k++)        	
		        		cmdStr = cmdStr + " " + L178_FT_upload[k];

		        javaExecSystemCmd2(cmdStr,3000);

						cmdStr = "java_Regular_Expression.csh " + L178_FT_upload[3] + " " + L178_FT_upload[5] + " " + "/usr/local/home/vol5/RealTimelog/L178/FT/";
		        javaExecSystemCmd2(cmdStr,3000);

//=================L389 STDF upload========================start
/*		        cmdStr = "ftp_sigurd_cp_all.csh";
		        for (int k = 0; k < L389_STDF_upload.length; k++)        	
		        		cmdStr = cmdStr + " " + L389_STDF_upload[k];

		        javaExecSystemCmd2(cmdStr,3000);
			 */
			//=================L022 FT STDF upload========================
			/*						cmdStr = "java_Regular_Expression_command4.csh " + "cp /usr/local/home/vol4/D10/prod/autoResult/summary/autosend/L022/ " + ".gz " + "/usr/local/home/vol5/stdf_backup/L022/";
		        javaExecSystemCmd2(cmdStr,60000);						
						cmdStr = "java_Regular_Expression.csh " + "/usr/local/home/vol4/D10/prod/autoResult/summary/autosend/L022/" + " " + L022_STDF_upload[5] + " " + "/usr/local/home/vol5/stdf_ftp/";
						javaExecSystemCmd2(cmdStr,60000); 

		        cmdStr = "ftp_sigurd_cp_all.csh";
		        for (int k = 0; k < L022_STDF_upload.length; k++)        	
		        		cmdStr = cmdStr + " " + L022_STDF_upload[k];

		        javaExecSystemCmd2(cmdStr,60000);

						cmdStr = "java_Regular_Expression_command3.csh " + "rm -f " + L022_STDF_upload[3] + " " + "HL022 " + ".gz";
						javaExecSystemCmd2(cmdStr,30000);

//=================L129 FT STDF upload========================
						cmdStr = "java_Regular_Expression_command4.csh " + "cp /usr/local/home/vol4/D10/prod/autoResult/summary/autosend/L129/ " + ".gz " + "/usr/local/home/vol5/stdf_backup/L129/";
		        javaExecSystemCmd2(cmdStr,60000);						
						cmdStr = "java_Regular_Expression.csh " + "/usr/local/home/vol4/D10/prod/autoResult/summary/autosend/L129/" + " " + L129_STDF_upload[5] + " " + "/usr/local/home/vol5/stdf_ftp/";
						javaExecSystemCmd2(cmdStr,60000); 

		        cmdStr = "ftp_sigurd_cp_all.csh";
		        for (int k = 0; k < L129_STDF_upload.length; k++)        	
		        		cmdStr = cmdStr + " " + L129_STDF_upload[k];

		        javaExecSystemCmd2(cmdStr,60000);

						cmdStr = "java_Regular_Expression_command3.csh " + "rm -f " + L129_STDF_upload[3] + " " + "HL129 " + ".gz";
						javaExecSystemCmd2(cmdStr,30000);
//=================F054 FT STDF upload========================
						cmdStr = "java_Regular_Expression_command4.csh " + "cp /usr/local/home/vol4/D10/prod/autoResult/summary/autosend/F054/ " + ".gz " + "/usr/local/home/vol5/stdf_backup/F054/";
		        javaExecSystemCmd2(cmdStr,3000);						
						cmdStr = "java_Regular_Expression.csh " + "/usr/local/home/vol4/D10/prod/autoResult/summary/autosend/F054/" + " " + F054_STDF_upload[5] + " " + "/usr/local/home/vol5/stdf_ftp/";
						javaExecSystemCmd2(cmdStr,3000); 

		        cmdStr = "ftp_sigurd_cp_all.csh";
		        for (int k = 0; k < F054_STDF_upload.length; k++)        	
		        		cmdStr = cmdStr + " " + F054_STDF_upload[k];

		        javaExecSystemCmd2(cmdStr,3000);

						cmdStr = "java_Regular_Expression_command3.csh " + "rm -f " + F054_STDF_upload[3] + " " + "HF054 " + ".gz";
						javaExecSystemCmd2(cmdStr,3000);
//=================L389 FT STDF upload========================
						cmdStr = "java_Regular_Expression_command4.csh " + "cp /usr/local/home/vol4/D10/prod/autoResult/summary/autosend/L389/ " + ".gz " + "/usr/local/home/vol5/stdf_backup/L389/";
		        javaExecSystemCmd2(cmdStr,3000);						
						cmdStr = "java_Regular_Expression.csh " + "/usr/local/home/vol4/D10/prod/autoResult/summary/autosend/L389/" + " " + L389_STDF_upload2[5] + " " + "/usr/local/home/vol5/stdf_ftp/";
						javaExecSystemCmd2(cmdStr,3000); 

		        cmdStr = "ftp_sigurd_cp_all.csh";
		        for (int k = 0; k < L389_STDF_upload2.length; k++)        	
		        		cmdStr = cmdStr + " " + L389_STDF_upload2[k];

		        javaExecSystemCmd2(cmdStr,3000);

						cmdStr = "java_Regular_Expression_command3.csh " + "rm -f " + L389_STDF_upload2[3] + " " + "HL389 " + ".gz";
						javaExecSystemCmd2(cmdStr,3000);         	        				
			 */		        
			//=========================================================end*/

			System.out.println("D10_STDF_upload_ALL  Waiting for next loop ...");
			Thread.sleep(180000);
		}            
	}

}