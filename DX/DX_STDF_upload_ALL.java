//20171113 Import NCFTP tool for HRID transferring loss by Cola.
//20171020 Add L449 run as MTK & add RealTimeLog transfer add Device:BJC10720,BM10720 by Cola.
//20170918 RealTimeLog transfer add Device:AZA10699 by Cola.
//20170807 STDF upload to Tango add FT for L227 by Cola.
//20170725 RealTimeLog transfer add Device:AHH10176 by Circle.
//20170721 RealTimeLog transfer add Device:AM10690 by Circle.
//20170621 MTK Cancel Send to OT+ , by Tango upload. by Cola. 
//20170612 RealTimeLog transfer add Device:AD10363 by Cola.
//20170609 RealTimeLog transfer add Device:BJC10299 by Cola.
//20170526 RealTimeLog transfer add Device:BM10299 by Cola.
//20170414 RealTimeLog transfer add Device: CHH10601 by Cola
//20170405 Revise TransferRealTimeLogforHRID code for data loss and CORR data don't sent.
//20170327 Source code improvement
//20170324 RealTimeLog transfer add Devie: BG10501, AHH10333 by Cola
//20170306 MTK all STDF upload to Tango. 20170306 
//20170224 Add BM10551 to UploadRealTimeLog_Device_ForMTK. by Cola
//20170214 Merge MTK schedule into one loop for time gap loss. by Cola  
//20170120 change RealTimelog upload source path. 
//20161025 Add AfterUploadBackupSTDF_Customer to check move to backup or not.
//20161003 Revise TransferSTDFtoTangoServer function to backup. by Cola
//20160812 Add F186 STDF Transfer to Tango Server and InfoFile to MES. by Cola
//20160801 Revise Trnasfer to Tango code to OP+ -> For STDF loss problem. by Cola

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

public class DX_STDF_upload_ALL extends JFrame{

//	static String[] UpdateTango_sourcePath = {"/dx_summary/CP/L022_STDF/AZA10176A/", "/dx_summary/CP/L129_STDF/AZA10335CW/", "/dx_summary/CP/L129_STDF/AZA10335CW_X/", "/dx_summary/CP/L022_STDF/AZA10333B/","/dx_summary/CP/L022_STDF/AHH10333D/","/dx_summary/CP/L022_STDF/AHH10333E/","/dx_summary/CP/L022_STDF/BHH10326CW/"/*Not to GB*/};
//	static String[] UpdateTango_uploadPath = {"/testerdata/L022/STDF/SGH/CP/", "/testerdata/L129/STDF/SGH/CP/", "/testerdata/L129/STDF/SGH/CP/", "/testerdata/L022/STDF/SGH/CP/","/testerdata/L022/STDF/SGH/CP/","/testerdata/L022/STDF/SGH/CP/","/testerdata/L022/STDF/SGH/CP/"};
//	static String[] UploadSTDFtoTango_Device = {"AZA10176","AZA10335","AZA10333","AHH10333","BHH10326"};
//	static String[] AZA10176A_STDF_upload = {"192.168.1.21","loader","loader","/dx_summary/CP/L022_STDF/AZA10176A/","/testerdata/L022/STDF/SGH/CP/",".std.gz"};
//  static String[] AZA10335CW_STDF_upload = {"192.168.1.21","loader","loader","/dx_summary/CP/L129_STDF/AZA10335CW/","/testerdata/L129/STDF/SGH/CP/",".std.gz"};
//  static String[] AZA10335CW_X_STDF_upload = {"192.168.1.21","loader","loader","/dx_summary/CP/L129_STDF/AZA10335CW_X/","/testerdata/L129/STDF/SGH/CP/",".std.gz"};
//  static String[] AHH10333E_STDF_upload = {"192.168.1.21","loader","loader","/dx_summary/CP/L022_STDF/AHH10333E/","/testerdata/L022/STDF/SGH/CP/",".std.gz"};
	static String[] UploadRealTimeLog_Device_ForMTK = {"AN10363","BHH10326","AHH10335","AZA10335","AZA10333","AE10363","BM10551","BG10501","AHH10333","CHH10601","BM10299", "BJC10299", "AD10363", "AM10690","AHH10176","AZA10699", "BJC10720", "BM10720"};
//	static String[] L129_AN10363C_realTimeLog_upload = {"192.168.1.76","sghpe2","sgh555pe2","/dx_summary/CP/L129/AN10363C/","/HRID/AN10363C/",".txt.gz"};
//	static String[] L022_BHH10326CW_realTimeLog_upload = {"192.168.1.76","sghpe2","sgh555pe2","/dx_summary/CP/L022/BHH10326CW/","/HRID/BHH10326CW/",".txt.gz"};
//	static String[] L129_AHH10335BW_realTimeLog_upload = {"192.168.1.76","sghpe2","sgh555pe2","/dx_summary/CP/L129/AHH10335BW/","/HRID/AHH10335BW/",".txt.gz"};
//	static String[] L129_AZA10335CW_realTimeLog_upload = {"192.168.1.76","sghpe2","sgh555pe2","/dx_summary/CP/L129/AZA10335CW/","/HRID/AZA10335CW/",".txt.gz"};
//	static String[] L022_AZA10333B_realTimeLog_upload = {"192.168.1.76","sghpe2","sgh555pe2","/dx_summary/CP/L022/AZA10333B/","/HRID/AZA10333B/",".txt.gz"};  //20160615
	static String[] UploadSTDFtoTango_Customer = {"F186","L227","L449"}, AfterUploadBackupSTDF_Customer = {"F186","L227","L449"};	//20171020
	static boolean TransferSTDFdata_flag = false;
	static String[] stdf_folder_content_array;  //check folder's std file
	static String TaiwanTime;	//now time
	static String innitial_path = "/dx_summary/CP/autoSent/";

	static int forloopNO = 0;
	static boolean check_CP_stdf_content_flag = false;           
//	static boolean check_FT_stdf_content_flag = false;
	static boolean check_CP_AHH10176C_stdf_content_flag = false;
	static boolean check_CP_BHD10238_stdf_content_flag = false;
	static boolean check_CP_AHD10289_stdf_content_flag = false; 
	static boolean check_CP_AHB10289BW_stdf_content_flag = false;        
	static boolean check_CP_BJC10299B_stdf_content_flag = false;      
//	static boolean check_CP_L121_TMGG68B_stdf_content_flag = false;
//	static boolean check_CP_L121_TMGG68C_T1EAZT_stdf_content_flag = false;
//	static boolean check_CP_L121_TMGG68C_T1EACT_stdf_content_flag = false;
	static boolean check_CP_L022_MTK_stdf_content_flag = false;
	static boolean check_CP_L129_MTK_stdf_content_flag = false;                                 
	static String[] L121_STDF_device_array;
	static String[] L022_STDF_device_array;
	static String[] L129_STDF_device_array;
	static String[] STDF_cusNO_array_FT;        
	static String[] device_array;
	static String[] STDF_device_array;
	static String[] CP_AZA10176A_stdf_folder_content_array_Oplus;  //check folder's std file
	static String[] CP_AHH10176C_stdf_folder_content_array_Oplus;  //check folder's std file
	static String[] CP_BJC10299B_stdf_folder_content_array_Oplus;  //check folder's std file	
	static String[] CP_BHD10238_stdf_folder_content_array;  //check folder's std file	
	static String[] CP_AHD10289_stdf_folder_content_array;  //check folder's std file	
	static String[] CP_AHB10289BW_stdf_folder_content_array;  //check folder's std file
	static String[] CP_L121_TMGG68B_stdf_folder_content_array;  //check folder's std file	
	static String[] CP_L121_TMGG68C_T1EAZT_stdf_folder_content_array;  //check folder's std file	
	static String[] CP_L121_TMGG68C_T1EACT_stdf_folder_content_array;  //check folder's std file
	static String[] CP_L022_MTK_stdf_folder_content_array;  //check folder's std file	
	static String[] CP_L129_MTK_stdf_folder_content_array;  //check folder's std file

	static String final_path = "";
	static String cmdStr = "";

	static String[] AZA10176A_STDF_upload_Oplus = {"192.168.1.70","sghpe2","sgh555pe2","/dx_summary/CP/L022_STDF/AZA10176A/","/L022/Oplus/",".std.gz"};
	static String[] AHH10176C_STDF_upload_Oplus = {"192.168.1.70","sghpe2","sgh555pe2","/dx_summary/CP/L022_STDF/AHH10176C/","/L022/Oplus/",".std.gz"};
	static String[] BJC10299B_STDF_upload_Oplus = {"192.168.1.70","sghpe2","sgh555pe2","/dx_summary/CP/L129_STDF/BJC10299B/","/L022/Oplus/",".std.gz"};
	static String[] DX_summary_upload = {"192.168.1.76","sghpe2","sgh555pe2","/dx_summary/CP/autoSent/","/CPSummary/DX/L022/",".sum"};
	static String[] BHD10238_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/dx_summary/CP/L022_STDF/BHD10238CW/","/L022/TMFQ18_BHD10238/",".std.gz"};
	static String[] TSMC_BHD10238_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/dx_summary/CP/L022_STDF/BHD10238CW/","/L022/TSMC_BHD10238/",".std.gz"};
	static String[] AHD10289_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/dx_summary/CP/L022_STDF/AHD10289DW/","/L022/TMFJ04_AHD10289/",".std.gz"};
	static String[] TSMC_AHD10289_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/dx_summary/CP/L022_STDF/AHD10289DW/","/L022/TSMC_AHD10289/",".std.gz"};
	static String[] SSMC_AHD10289_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/dx_summary/CP/L022_STDF/AHD10289DW/","/L022/SSMC_AHD10289/",".std.gz"};
	
	static String[] SSMC_AHB10289BW_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/dx_summary/CP/L022_STDF/AHB10289BW/","/L022/SSMC_AHD10289/",".std.gz"};
	static String[] L121_iEDAsummary_upload = {"192.168.1.21","loader","loader","/dx_summary/CP/autoSent/L121/iEDATransfer/","/upmap/tester/tsmc/",".sum"};
	static String[] L121_TMGG68B_STDF_upload = {"192.168.1.21","loader","loader","/dx_summary/CP/L121_STDF/TMGG68B/","/testerdata/L121/STDF/SGH/CP/",".std.gz"};
	static String[] L121_TMGG68C_T1EAZT_STDF_upload = {"192.168.1.21","loader","loader","/dx_summary/CP/L121_STDF/TMGG68C_T1EAZT/","/testerdata/L121/STDF/SGH/CP/",".std.gz"};
	static String[] L121_TMGG68C_T1EACT_STDF_upload = {"192.168.1.21","loader","loader","/dx_summary/CP/L121_STDF/TMGG68C_T1EACT/","/testerdata/L121/STDF/SGH/CP/",".std.gz"};
//	static String[] L022_MTK_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/dx_summary/CP/L022_STDF/","/L129/MTK_CP_all_STDF/",".std.gz"};
//	static String[] L129_MTK_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/dx_summary/CP/L129_STDF/","/L129/MTK_CP_all_STDF/",".std.gz"};
	static SimpleDateFormat nowdate = new java.text.SimpleDateFormat("yyyyMMdd");
	
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
	
	//=================STDF upload to global foundries server ======================start
	//String[] AZA10335CW_STDF_upload = {"192.168.1.21","loader","loader","/dx_summary/CP/L129_STDF/AZA10335CW/","/testerdata/L129/STDF/SGH/CP/",".std.gz"	
	static void TransferSTDFtoTangoServer(String uploadInfomation[]){
		File CP_stdf_process_file_folder = new File(uploadInfomation[3]);	
		if (CP_stdf_process_file_folder.isDirectory()){//check folder's std file
			System.out.println("Upload STDF to Tango Server !! source path: " + uploadInfomation[3]);
			stdf_folder_content_array = CP_stdf_process_file_folder.list();
			if (stdf_folder_content_array.length != 0){
				if (stdf_folder_content_array.length > 15){
					forloopNO = 15;
				}else{
					forloopNO = stdf_folder_content_array.length;
				}

				for (int k = 0; k < forloopNO; k++){
					if(stdf_folder_content_array[k].indexOf(".std.gz") != -1)
						check_CP_stdf_content_flag = true;
					else 
						check_CP_stdf_content_flag = false;

//					if(stdf_folder_content_array[k].indexOf("DCORR_HL227") != -1) //Temp for L227. 20171103
//						check_CP_stdf_content_flag = false;
					
					if (check_CP_stdf_content_flag == true){				            										            											

						cmdStr = "AZA10176A_ftp_sigurd_cp_all.csh";
						for (int i = 0; i < 5; i++)        	
							cmdStr = cmdStr + " " + uploadInfomation[i];						

						cmdStr = cmdStr + " " + stdf_folder_content_array[k] ;
						javaExecSystemCmd2(cmdStr,10000);
						
						for(int I = 0 ; I < AfterUploadBackupSTDF_Customer.length ; I++){  //Serch STDF Data  //後面沒有其他排程的才移到Backup by Cola. 20161025
							if(uploadInfomation[3].indexOf(AfterUploadBackupSTDF_Customer[I]) != -1){
								cmdStr = "AZA10176A_java_Regular_Expression.csh " + uploadInfomation[3] + " " +
										stdf_folder_content_array[k] + " " + uploadInfomation[3] + "backup/";
								javaExecSystemCmd2(cmdStr,5000);
							}
						}
					}
				}
			}else{
				System.out.println("NO file......waitting......");
			}
		}
	}
	//=================STDF upload to global foundries server======================end	
	//=================RealTimelog Upload Founction======================start
//	static String[] L129_AN10363C_realTimeLog_upload = {"192.168.1.76","sghpe2","sgh555pe2","/dx_summary/CP/L129/AN10363C/","/HRID/AN10363C/",".txt.gz"};
	static void TransferRealTimeLogforHRID(String DeviceName){
		String[] CP_RealTimelog_Device_array;  //Search Device
		String[] CP_RealTimelog_File_array;  //check .txt file
		String[] CP_CustomerID = {"L022","L129","L449"}; //20171020
		String SourcePath = "";
		String UploadPath = "";
		boolean createDirFlag;

		for(int c = 0; c < CP_CustomerID.length; c++){
			File CP_RealTimelog_customer_folder_content = new File("/dx_summary/CP/" + CP_CustomerID[c]);
			CP_RealTimelog_Device_array = CP_RealTimelog_customer_folder_content.list();

			for (int d = 0; d < CP_RealTimelog_Device_array.length; d++){  //Search Device Folder
				if(CP_RealTimelog_Device_array[d].indexOf(DeviceName) != -1){
					SourcePath = "/dx_summary/CP/" + CP_CustomerID[c] + "/" + CP_RealTimelog_Device_array[d] +"/HRID_upload/"; //change path to "HRID_upload" by Cola. 20170120
					UploadPath = "/HRID/" + DeviceName + "/";

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
	}
	//=================RealTimelog Upload Function======================end

	static void AZA10176A_Upload(String UploadFileName){
		//=================AZA10176A STDF upload to O+ server======================start
		//AZA10176A_STDF_upload_Oplus = {"192.168.1.70","sghpe2","sgh555pe2","/dx_summary/CP/L022_STDF/AZA10176A/","/L022/Oplus/",".std.gz"};		

		AZA10176A_STDF_upload_Oplus[4] = "/L022/Oplus/";
		TaiwanTime = nowdate.format(new java.util.Date());
		System.out.println(TaiwanTime);	
		AZA10176A_STDF_upload_Oplus[4] = AZA10176A_STDF_upload_Oplus[4] + TaiwanTime + "/";
		System.out.println(AZA10176A_STDF_upload_Oplus[4]);

		cmdStr = "AZA10176A_ftp_sigurd_cp_all.csh";
		for (int i = 0; i < 5; i++)        	
			cmdStr = cmdStr + " " + AZA10176A_STDF_upload_Oplus[i];						

		cmdStr = cmdStr + " " + UploadFileName;
		javaExecSystemCmd2(cmdStr,10000);

		//=================AZA10176A STDF upload to O+ server======================end		
	}
	static void AHH10176C_Upload(String UploadFileName){
		//=================AHH10176C STDF upload to O+ server======================start
		//AHH10176C_STDF_upload_Oplus = {"192.168.1.70","sghpe2","sgh555pe2","/dx_summary/CP/L022_STDF/AHH10176C/","/L022/Oplus/",".std.gz"};		
//		File CP_AHH10176C_stdf_folder_content_array_file_Oplus = new File("/dx_summary/CP/L022_STDF/AHH10176C/");
//		if (CP_AHH10176C_stdf_folder_content_array_file_Oplus.isDirectory()){//check folder's std file			            						
//
//			CP_AHH10176C_stdf_folder_content_array_Oplus = CP_AHH10176C_stdf_folder_content_array_file_Oplus.list();
//
//			if (CP_AHH10176C_stdf_folder_content_array_Oplus.length != 0){
//
//				if (CP_AHH10176C_stdf_folder_content_array_Oplus.length > 15){
//					forloopNO = 15;
//				}else{
//					forloopNO = CP_AHH10176C_stdf_folder_content_array_Oplus.length;
//				}

				AHH10176C_STDF_upload_Oplus[4] = "/L022/Oplus/";
				TaiwanTime = nowdate.format(new java.util.Date());
				System.out.println(TaiwanTime);	
				AHH10176C_STDF_upload_Oplus[4] = AHH10176C_STDF_upload_Oplus[4] + TaiwanTime + "/";
				System.out.println(AHH10176C_STDF_upload_Oplus[4]);

//				for (int k = 0; k < forloopNO; k++){
//					check_CP_AHH10176C_stdf_content_flag = false;
//					if(CP_AHH10176C_stdf_folder_content_array_Oplus[k].indexOf(".std.gz") != -1)
//						check_CP_AHH10176C_stdf_content_flag = true;
//
//					if (check_CP_AHH10176C_stdf_content_flag == true){				            										            											

						cmdStr = "AZA10176A_ftp_sigurd_cp_all.csh";
						for (int i = 0; i < 5; i++)        	
							cmdStr = cmdStr + " " + AHH10176C_STDF_upload_Oplus[i];						

						cmdStr = cmdStr + " " + UploadFileName;
						javaExecSystemCmd2(cmdStr,10000);

//					}
//				}
//			}else{
//				System.out.println("NO file......waitting......");
//			}
//		}		
		//=================AHH10176C STDF upload to O+ server======================end			
	}
	static void BJC10299B_Upload(String UploadFileName){
		//=================BJC10299B STDF upload to O+ server======================start
		//BJC10299B_STDF_upload_Oplus = {"192.168.1.70","sghpe2","sgh555pe2","/dx_summary/CP/L129_STDF/BJC10299B/","/L022/Oplus/",".std.gz"};

		BJC10299B_STDF_upload_Oplus[4] = "/L022/Oplus/";
		TaiwanTime = nowdate.format(new java.util.Date());
		System.out.println(TaiwanTime);	
		BJC10299B_STDF_upload_Oplus[4] = BJC10299B_STDF_upload_Oplus[4] + TaiwanTime + "/";
		System.out.println(BJC10299B_STDF_upload_Oplus[4]);			            										            											

		cmdStr = "AZA10176A_ftp_sigurd_cp_all.csh";
		for (int i = 0; i < 5; i++)        	
			cmdStr = cmdStr + " " + BJC10299B_STDF_upload_Oplus[i];						

		cmdStr = cmdStr + " " + UploadFileName;
		javaExecSystemCmd2(cmdStr,10000);

		//=================BJC10299B STDF upload to O+ server======================end		
	}
	static void BHD10238CW_Upload(String UploadFileName){
		//=================BHD10238 STDF upload======================start
		//BHD10238_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/dx_summary/CP/L022_STDF/BHD10238CW/","/L022/TMFQ18_BHD10238/",".std.gz"};
		//TSMC_BHD10238_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/dx_summary/CP/L022_STDF/BHD10238CW/","/L022/TSMC_BHD10238/",".std.gz"};

//		File CP_BHD10238_stdf_folder_content_array_file = new File("/dx_summary/CP/L022_STDF/BHD10238CW/");
//		if (CP_BHD10238_stdf_folder_content_array_file.isDirectory()){//check folder's std file				            							
//
//			CP_BHD10238_stdf_folder_content_array = CP_BHD10238_stdf_folder_content_array_file.list();	
//			for (int k = 0; k < CP_BHD10238_stdf_folder_content_array.length; k++){
//				check_CP_BHD10238_stdf_content_flag = false;
//				if(CP_BHD10238_stdf_folder_content_array[k].indexOf(".std.gz") != -1)
//					check_CP_BHD10238_stdf_content_flag = true;
//
//				if (check_CP_BHD10238_stdf_content_flag == true){				            										            						

					cmdStr = "AZA10176A_java_Regular_Expression.csh " + BHD10238_STDF_upload[3] + " " + "EQC " + BHD10238_STDF_upload[3] + "backup";
					javaExecSystemCmd2(cmdStr,3000);			

					cmdStr = "AZA10176A_ftp_sigurd_cp_all.csh";
					for (int i = 0; i < 5; i++)       	
						cmdStr = cmdStr + " " + BHD10238_STDF_upload[i];	
					cmdStr = cmdStr + " " + UploadFileName;
					javaExecSystemCmd2(cmdStr,10000);

					cmdStr = "AZA10176A_ftp_sigurd_cp_all.csh";
					for (int i = 0; i < 5; i++)      //20150305 autoSent to TSMC by ChiaHui    	
						cmdStr = cmdStr + " " + TSMC_BHD10238_STDF_upload [i];
					cmdStr = cmdStr + " " + UploadFileName;
					javaExecSystemCmd2(cmdStr,10000);			        							        

//					cmdStr = "AZA10176A_java_Regular_Expression.csh " + BHD10238_STDF_upload[3] + " " + BHD10238_STDF_upload[5] + " " + "/dx_summary/CP/L022_STDF/BHD10238CW/backup/";
//					javaExecSystemCmd2(cmdStr,3000); 
//					break;
//				}
//			}
//		}
	}
	static void AHD10289DW_Upload(String UploadFileName){
		//=================10289 STDF upload======================start
		//AHD10289_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/dx_summary/CP/L022_STDF/AHD10289DW/","/L022/TMFJ04_AHD10289/",".std.gz"};
		//TSMC_AHD10289_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/dx_summary/CP/L022_STDF/AHD10289DW/","/L022/TSMC_AHD10289/",".std.gz"};
		//SSMC_AHD10289_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/dx_summary/CP/L022_STDF/AHD10289DW/","/L022/SSMC_AHD10289/",".std.gz"};

//		File CP_AHD10289_stdf_folder_content_array_file = new File("/dx_summary/CP/L022_STDF/AHD10289DW/");
//		if (CP_AHD10289_stdf_folder_content_array_file.isDirectory()){//check folder's std file				            							
//
//			CP_AHD10289_stdf_folder_content_array = CP_AHD10289_stdf_folder_content_array_file.list();	
//			for (int k = 0; k < CP_AHD10289_stdf_folder_content_array.length; k++){
//				check_CP_AHD10289_stdf_content_flag = false;
//				if(CP_AHD10289_stdf_folder_content_array[k].indexOf(".std.gz") != -1)
//					check_CP_AHD10289_stdf_content_flag = true;
//
//				if (check_CP_AHD10289_stdf_content_flag == true){				            										            						

					cmdStr = "AZA10176A_java_Regular_Expression.csh " + AHD10289_STDF_upload[3] + " " + "EQC " + AHD10289_STDF_upload[3] + "backup";
					javaExecSystemCmd2(cmdStr,3000);								

					cmdStr = "AZA10176A_ftp_sigurd_cp_all.csh";
					for (int i = 0; i < 5; i++)       	
						cmdStr = cmdStr + " " + AHD10289_STDF_upload[i];
					cmdStr = cmdStr + " " + UploadFileName;
					javaExecSystemCmd2(cmdStr,10000);

					cmdStr = "AZA10176A_ftp_sigurd_cp_all.csh";
					for (int i = 0; i < 5; i++)        //20150305 autoSent to TSMC by ChiaHui    	
						cmdStr = cmdStr + " " + TSMC_AHD10289_STDF_upload [i];	
					cmdStr = cmdStr + " " + UploadFileName;
					javaExecSystemCmd2(cmdStr,10000);
					
					cmdStr = "AZA10176A_ftp_sigurd_cp_all.csh";
					for (int i = 0; i < 5; i++)        //20150313 autoSent to SSMC by ChiaHui (Chou_Roy)	
						cmdStr = cmdStr + " " + SSMC_AHD10289_STDF_upload [i];
					cmdStr = cmdStr + " " + UploadFileName;
					javaExecSystemCmd2(cmdStr,10000);
					
//					cmdStr = "AZA10176A_java_Regular_Expression.csh " + AHD10289_STDF_upload[3] + " " + AHD10289_STDF_upload[5] + " " + "/dx_summary/CP/L022_STDF/AHD10289DW/backup/";
//					javaExecSystemCmd2(cmdStr,3000); 
//					break;
//				}
//			}
//		}		
		//=================10289 STDF upload======================end
	}
	static void AHB10289BW_Upload(String UploadFileName){
		//=================AHB10289BW STDF upload======================start
		//SSMC_AHB10289BW_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/dx_summary/CP/L022_STDF/AHB10289BW/","/L022/SSMC_AHD10289/",".std.gz"};

//		File CP_AHB10289BW_stdf_folder_content_array_file = new File("/dx_summary/CP/L022_STDF/AHB10289BW/");
//		if (CP_AHB10289BW_stdf_folder_content_array_file.isDirectory()){//check folder's std file				            							
//
//			CP_AHB10289BW_stdf_folder_content_array = CP_AHB10289BW_stdf_folder_content_array_file.list();	
//			for (int k = 0; k < CP_AHB10289BW_stdf_folder_content_array.length; k++){
//				check_CP_AHB10289BW_stdf_content_flag = false;
//				if(CP_AHB10289BW_stdf_folder_content_array[k].indexOf(".std.gz") != -1)
//					check_CP_AHB10289BW_stdf_content_flag = true;
//
//				if (check_CP_AHB10289BW_stdf_content_flag == true){				            										            						

					cmdStr = "AZA10176A_java_Regular_Expression.csh " + SSMC_AHB10289BW_STDF_upload[3] + " " + "EQC " + SSMC_AHB10289BW_STDF_upload[3] + "backup";
					javaExecSystemCmd2(cmdStr,3000);								

					cmdStr = "AZA10176A_ftp_sigurd_cp_all.csh";
					for (int i = 0; i < 5; i++)        //20150730 autoSent to SSMC by ChiaHui (Chou_Roy)	
						cmdStr = cmdStr + " " + SSMC_AHB10289BW_STDF_upload [i];
					cmdStr = cmdStr + " " + UploadFileName;
					javaExecSystemCmd2(cmdStr,10000);
					
//					cmdStr = "AZA10176A_java_Regular_Expression.csh " + SSMC_AHB10289BW_STDF_upload[3] + " " + SSMC_AHB10289BW_STDF_upload[5] + " " + "/dx_summary/CP/L022_STDF/AHB10289BW/backup/";
//					javaExecSystemCmd2(cmdStr,3000); 
//					break;
//				}
//			}
//		}		
		//=================AHB10289BW STDF upload======================end
	}

	public static void main(String[] args) throws Exception
	{
		nowdate.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		               

		File f = new File(innitial_path);
		File L121_STDF_file = new File("/dx_summary/CP/L121_STDF/");       
		File MTK_L022_STDF_file = new File("/dx_summary/CP/L022_STDF/");
		File MTK_L129_STDF_file = new File("/dx_summary/CP/L129_STDF/"); 
		File DX_summary_CP_Data = new File("/dx_summary/CP/");
		File DX_summary_FT_Data = new File("/dx_summary/FT/");	//20170807
		
		String SourcePath = "";
		String UploadPath = "";
		String STDF_Transfer_Customer = "";
		String[] dx_summary_dir_array;

		while(true){
			//==========F186 STDF upload to Tango and InfoFile upload to MES==========start
			dx_summary_dir_array = DX_summary_CP_Data.list();
			for(int dirIndex = 0 ; dirIndex < dx_summary_dir_array.length ; dirIndex++){ // 取得/dx_summary/CP/ 底下資料
				TransferSTDFdata_flag = false;
				for(int Index = 0 ; Index < UploadSTDFtoTango_Customer.length ; Index++){  //Serch STDF Data
//					System.out.println("dx_summary_dir_array[ "+ dirIndex +" ]=" + dx_summary_dir_array[dirIndex] + " for " + UploadSTDFtoTango_Customer[Index]);
					if(dx_summary_dir_array[dirIndex].equalsIgnoreCase(UploadSTDFtoTango_Customer[Index]+"_STDF")){
						System.out.println("!!Serch Directory: " + UploadSTDFtoTango_Customer[Index]+"_STDF");
						TransferSTDFdata_flag = true;
						STDF_Transfer_Customer = UploadSTDFtoTango_Customer[Index];
					}	
				}
				if (TransferSTDFdata_flag){ //為指定要傳送的客戶別才傳
					File dx_summary_CP_Customer_array_file = new File("/dx_summary/CP/" + dx_summary_dir_array[dirIndex]);
					if (dx_summary_CP_Customer_array_file.isDirectory()){

						STDF_device_array = dx_summary_CP_Customer_array_file.list();
						//						System.out.println("L022 outer loop");
						for (int STDF_device_array_counter = 0; STDF_device_array_counter < STDF_device_array.length; STDF_device_array_counter++){
							System.out.println("!!!Device:" + STDF_device_array[STDF_device_array_counter]);

							File stdf_folder_content_array_file = new File("/dx_summary/CP/" + dx_summary_dir_array[dirIndex] + "/" + STDF_device_array[STDF_device_array_counter]);
							if (stdf_folder_content_array_file.isDirectory()){//check folder's std file			            						

								stdf_folder_content_array = stdf_folder_content_array_file.list();

								if (stdf_folder_content_array.length != 0){

									SourcePath = "/dx_summary/CP/" + dx_summary_dir_array[dirIndex] + "/" + STDF_device_array[STDF_device_array_counter] + "/";
									UploadPath = "/testerdata/" + STDF_Transfer_Customer + "/STDF/SGH/CP/";
									System.out.println("SourcePath: "+SourcePath+" ,UploadPath: "+UploadPath);
									cmdStr = "mkdir " + SourcePath + "backup";
									javaExecSystemCmd2(cmdStr,1000);
									String[] STDF_upload = {"192.168.1.21","loader","loader",SourcePath,UploadPath,".std.gz"};
									TransferSTDFtoTangoServer(STDF_upload); //Upload to Tango	
									
									//Upload F186 InfoFile to 192.168.1.76 for CP by Cola. 20160707-----Start
									if(dx_summary_dir_array[dirIndex].equals("F186_STDF")){  
										for (int k = 0; k < stdf_folder_content_array.length; k++){
											if(stdf_folder_content_array[k].indexOf("I_") != -1){  
												System.out.println("Upload F186 CP InfoFile");
												cmdStr = "java_Regular_Expression_command4.csh " + "cp " + SourcePath + "/ " + stdf_folder_content_array[k] + " " + SourcePath + "/backup";
												javaExecSystemCmd2(cmdStr,3000);

												//												String[] AZA10176A_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/usr/local/home/vol5/STDF/L022/CP/AZA10176A/","/L022_GLOBAL/From_Sigurd/STDF_data",".std.gz"};
												cmdStr = "ftp_sigurd_cp_all.csh 192.168.1.76 lantiq_sg vkrXq9_D " + SourcePath + " " + "/F186/STDF/VIA_CP " + stdf_folder_content_array[k];
												javaExecSystemCmd2(cmdStr, 3000);                       

												cmdStr = "java_Regular_Expression_command3.csh " + "rm -f " + SourcePath + "/ " + stdf_folder_content_array[k] + " " + "*";
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
			//==========F186 STDF upload to Tango and InfoFile upload to MES==========End
			
			//==========FT STDF upload to Tango 20170807==========start
			dx_summary_dir_array = DX_summary_FT_Data.list();
			for(int dirIndex = 0 ; dirIndex < dx_summary_dir_array.length ; dirIndex++){ // 取得/dx_summary/FT/ 底下資料
				TransferSTDFdata_flag = false;
				for(int Index = 0 ; Index < UploadSTDFtoTango_Customer.length ; Index++){  //Serch STDF Data
//					System.out.println("dx_summary_dir_array[ "+ dirIndex +" ]=" + dx_summary_dir_array[dirIndex] + " for " + UploadSTDFtoTango_Customer[Index]);
					if(dx_summary_dir_array[dirIndex].equalsIgnoreCase(UploadSTDFtoTango_Customer[Index]+"_STDF")){
						System.out.println("!!Serch Directory: " + UploadSTDFtoTango_Customer[Index]+"_STDF");
						TransferSTDFdata_flag = true;
						STDF_Transfer_Customer = UploadSTDFtoTango_Customer[Index];
					}	
				}
				if (TransferSTDFdata_flag){ //為指定要傳送的客戶別才傳
					File dx_summary_FT_Customer_array_file = new File("/dx_summary/FT/" + dx_summary_dir_array[dirIndex]);
					if (dx_summary_FT_Customer_array_file.isDirectory()){

						STDF_device_array = dx_summary_FT_Customer_array_file.list();
						//						System.out.println("L022 outer loop");
						for (int STDF_device_array_counter = 0; STDF_device_array_counter < STDF_device_array.length; STDF_device_array_counter++){
							System.out.println("!!!Device:" + STDF_device_array[STDF_device_array_counter]);

							File FT_stdf_folder_content_array_file = new File("/dx_summary/FT/" + dx_summary_dir_array[dirIndex] + "/" + STDF_device_array[STDF_device_array_counter]);
							if (FT_stdf_folder_content_array_file.isDirectory()){//check folder's std file			            						

								stdf_folder_content_array = FT_stdf_folder_content_array_file.list();

								if (stdf_folder_content_array.length != 0){

									SourcePath = "/dx_summary/FT/" + dx_summary_dir_array[dirIndex] + "/" + STDF_device_array[STDF_device_array_counter] + "/";
									UploadPath = "/testerdata/" + STDF_Transfer_Customer + "/STDF/SGH/FT/";
									System.out.println("SourcePath: "+SourcePath+" ,UploadPath: "+UploadPath);
									cmdStr = "mkdir " + SourcePath + "backup";
									javaExecSystemCmd2(cmdStr,1000);
									String[] STDF_upload = {"192.168.1.21","loader","loader",SourcePath,UploadPath,".std.gz"};
									TransferSTDFtoTangoServer(STDF_upload); //Upload to Tango	
									
								}
							}
						}
					}
				}			
			}
			//==========FT STDF upload to Tango 20170807==========End
			
			
			
			
			
/*
			//=================iEDA summary upload to tango server======================start

			File CP_L121_iEDAsummaryPath = new File("/dx_summary/CP/autoSent/L121/iEDATransfer/");
			if (CP_L121_iEDAsummaryPath.isDirectory()){//check folder's std file			            										            										            											

				cmdStr = "AZA10176A_ftp_sigurd_cp_all.csh";
				for (int i = 0; i <= 5; i++)        	
					cmdStr = cmdStr + " " + L121_iEDAsummary_upload[i];						

				javaExecSystemCmd2(cmdStr,3000);

				cmdStr = "AZA10176A_java_Regular_Expression.csh " + L121_iEDAsummary_upload[3] + " " +
						L121_iEDAsummary_upload[5] + " " + "/dx_summary/CP/autoSent/L121/iEDAbackup/";
				javaExecSystemCmd2(cmdStr,1000);			
			}		
			//=================iEDA summary upload to tango server======================end	
			//=================L121 TMGG68B STDF upload to tango server======================start	

			if (L121_STDF_file.isDirectory()){

				L121_STDF_device_array = L121_STDF_file.list();
				System.out.println("L121 outer loop");
				for (int L121_STDF_device_array_counter = 0; L121_STDF_device_array_counter < L121_STDF_device_array.length; L121_STDF_device_array_counter++){
					System.out.println(L121_STDF_device_array[L121_STDF_device_array_counter]);

					File CP_L121_TMGG68B_stdf_folder_content_array_file = new File("/dx_summary/CP/L121_STDF/" + L121_STDF_device_array[L121_STDF_device_array_counter]);
					if (CP_L121_TMGG68B_stdf_folder_content_array_file.isDirectory()){//check folder's std file			            						

						CP_L121_TMGG68B_stdf_folder_content_array = CP_L121_TMGG68B_stdf_folder_content_array_file.list();

						if (CP_L121_TMGG68B_stdf_folder_content_array.length != 0){

							if (CP_L121_TMGG68B_stdf_folder_content_array.length > 15){
								forloopNO = 15;
							}else{
								forloopNO = CP_L121_TMGG68B_stdf_folder_content_array.length;
							}

							for (int k = 0; k < forloopNO; k++){
								if(CP_L121_TMGG68B_stdf_folder_content_array[k].indexOf(".std.gz") != -1)
									check_CP_L121_TMGG68B_stdf_content_flag = true;

								if (check_CP_L121_TMGG68B_stdf_content_flag == true){				            										            

									cmdStr = "mkdir " + L121_STDF_device_array[L121_STDF_device_array_counter] + "/backup";
									javaExecSystemCmd2(cmdStr,1000);

									cmdStr = "AZA10176A_java_Regular_Expression1.csh " + "/dx_summary/CP/L121_STDF/" + L121_STDF_device_array[L121_STDF_device_array_counter] + "/ " + "CORR " + "/dx_summary/CP/L121_STDF/" + L121_STDF_device_array[L121_STDF_device_array_counter] + "/backup";
									javaExecSystemCmd2(cmdStr,3000);																											
									cmdStr = "AZA10176A_ftp_sigurd_cp_all.csh";
									for (int i = 0; i < 5; i++){   	
										if (i != 3){
											cmdStr = cmdStr + " " + L121_TMGG68B_STDF_upload[i];
										}else{
											cmdStr = cmdStr + " " + "/dx_summary/CP/L121_STDF/" + L121_STDF_device_array[L121_STDF_device_array_counter];
										}

									}											

									cmdStr = cmdStr + " " + CP_L121_TMGG68B_stdf_folder_content_array[k] ;
									javaExecSystemCmd2(cmdStr,3000);

									cmdStr = "AZA10176A_java_Regular_Expression.csh " + "/dx_summary/CP/L121_STDF/" + L121_STDF_device_array[L121_STDF_device_array_counter] + "/ " +
											CP_L121_TMGG68B_stdf_folder_content_array[k] + " " + "/dx_summary/CP/L121_STDF/" + L121_STDF_device_array[L121_STDF_device_array_counter] + "/backup/";
									javaExecSystemCmd2(cmdStr,10000); 
								}
							}
						}else{
							System.out.println("NO file......waitting......");
						}
					}
				}
			}		
			//=================L121 STDF TMGG68B upload to tango server======================end
*/
			//=================AZA10335CW STDF upload to global foundries server ======================start
			//String[] AZA10335CW_STDF_upload = {"192.168.1.21","loader","loader","/dx_summary/CP/L129_STDF/AZA10335CW/","/testerdata/L129/STDF/SGH/CP/",".std.gz"
/*			System.out.println("Upload to Tango Server-----Start");

			String[] Device_array;  //Search Device
			String[] CP_CustomerID = {"L022","L129"};
			String SourcePath = "";
			String UploadPath = "";

			for(int c = 0; c < CP_CustomerID.length; c++){
				File customer_folder_content = new File("/dx_summary/CP/" + CP_CustomerID[c] + "_STDF");
				Device_array = customer_folder_content.list();

				for (int d2 = 0; d2 < Device_array.length; d2++){  //Search Device Folder
					for(int d1 = 0 ; d1 < UploadSTDFtoTango_Device.length ; d1 ++){
						if(Device_array[d2].indexOf(UploadSTDFtoTango_Device[d1]) != -1){
							SourcePath = "/dx_summary/CP/" + CP_CustomerID[c] + "_STDF/" + Device_array[d2] +"/";
							UploadPath = "/testerdata/" + CP_CustomerID[c] + "/STDF/SGH/CP/";

							String[] GlobalFoundries_STDF_upload = {"192.168.1.21","loader","loader",SourcePath,UploadPath,".std.gz"};
							TransferSTDFtoTangoServer(GlobalFoundries_STDF_upload); //Upload to Tango
						}
					}
				}
			}
			System.out.println("Upload to Tango Server-----End");
*/			//=================AZA10335CW STDF upload to global foundries server======================end	
			//=================L022 L129 STDF upload to OP+======================start	
			if (MTK_L022_STDF_file.isDirectory()){
				L022_STDF_device_array = MTK_L022_STDF_file.list();
				System.out.println("L022 outer loop");
				for (int L022_STDF_device_array_counter = 0; L022_STDF_device_array_counter < L022_STDF_device_array.length; L022_STDF_device_array_counter++){
					System.out.println("L022 device:" + L022_STDF_device_array[L022_STDF_device_array_counter]);
					File CP_L022_MTK_stdf_folder_content_array_file = new File("/dx_summary/CP/L022_STDF/" + L022_STDF_device_array[L022_STDF_device_array_counter]);
					if (CP_L022_MTK_stdf_folder_content_array_file.isDirectory()){//check folder's std file			            						

						CP_L022_MTK_stdf_folder_content_array = CP_L022_MTK_stdf_folder_content_array_file.list();

						if (CP_L022_MTK_stdf_folder_content_array.length != 0){

							
							
						//L022 STDF Transfer to Tango(Tango will upload to global foundries server) by Cola-----Start
//							for(int d1 = 0 ; d1 < UploadSTDFtoTango_Device.length ; d1 ++){
////								System.out.println("Device: "+L022_STDF_device_array[L022_STDF_device_array_counter]+" ,Device2: "+UploadSTDFtoTango_Device[d1]+ " ,indexOf: "+ L022_STDF_device_array[L022_STDF_device_array_counter].indexOf(UploadSTDFtoTango_Device[d1]));
//								if(L022_STDF_device_array[L022_STDF_device_array_counter].indexOf(UploadSTDFtoTango_Device[d1]) != -1){	//Remark for send all MTK STDF. 201703016
//									SourcePath = MTK_L022_STDF_file + "/" + L022_STDF_device_array[L022_STDF_device_array_counter] +"/";
//									UploadPath = "/testerdata/L022/STDF/SGH/CP/";
////                                    System.out.println("SourcePath: "+SourcePath+" ,UploadPath: "+UploadPath);
//									String[] GlobalFoundries_STDF_upload = {"192.168.1.21","loader","loader",SourcePath,UploadPath,".std.gz"};
//									TransferSTDFtoTangoServer(GlobalFoundries_STDF_upload); //Upload to Tango
//								}
//							}
						//L022 STDF Transfer to Tango(Tango will upload to global foundries server) by Cola-----End
							if (CP_L022_MTK_stdf_folder_content_array.length > 15){
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
										cmdStr = "chmod 777 " + "/dx_summary/CP/L022_STDF/" + L022_STDF_device_array[L022_STDF_device_array_counter] + "/backup";
										javaExecSystemCmd2(cmdStr,1000);
									}
									if(CP_L022_MTK_stdf_folder_content_array[k].indexOf("-CORR") == -1){ //don't send CORR data. 20170306
										//L022 All STDF Transfer to Tango(Tango will upload to global foundries server) by Cola. 20170306-----Start
										SourcePath = MTK_L022_STDF_file + "/" + L022_STDF_device_array[L022_STDF_device_array_counter] +"/";
										UploadPath = "/testerdata/L022/STDF/SGH/CP/";
										//                                    System.out.println("SourcePath: "+SourcePath+" ,UploadPath: "+UploadPath);
										String[] STDF_upload = {"192.168.1.21","loader","loader",SourcePath,UploadPath,".std.gz"};

										cmdStr = "AZA10176A_ftp_sigurd_cp_all.csh";
										for (int i = 0; i < 5; i++)        	
											cmdStr = cmdStr + " " + STDF_upload[i];						

										cmdStr = cmdStr + " " + CP_L022_MTK_stdf_folder_content_array[k] ;
										javaExecSystemCmd2(cmdStr,15000);
										//L022 All STDF Transfer to Tango(Tango will upload to global foundries server) by Cola. 20170306-----End
									/* Cancel Send to OT+ , by Tango upload. 20170621 
										cmdStr = "AZA10176A_ftp_sigurd_cp_all.csh";
										for (int i = 0; i < 5; i++){   	
											if (i != 3){
												cmdStr = cmdStr + " " + L022_MTK_STDF_upload[i];
											}else{
												cmdStr = cmdStr + " " + "/dx_summary/CP/L022_STDF/" + L022_STDF_device_array[L022_STDF_device_array_counter];
											}
										}											
										cmdStr = cmdStr + " " + CP_L022_MTK_stdf_folder_content_array[k] ;
										javaExecSystemCmd2(cmdStr,10000);
									*/
										//Revise code. 20170214-----Start	
										if(L022_STDF_device_array[L022_STDF_device_array_counter].equals("AZA10176A")) 
											AZA10176A_Upload(CP_L022_MTK_stdf_folder_content_array[k]);
										if(L022_STDF_device_array[L022_STDF_device_array_counter].equals("AHH10176C")) 
											AHH10176C_Upload(CP_L022_MTK_stdf_folder_content_array[k]);
										if(L022_STDF_device_array[L022_STDF_device_array_counter].equals("BHD10238CW")) 
											BHD10238CW_Upload(CP_L022_MTK_stdf_folder_content_array[k]);
										if(L022_STDF_device_array[L022_STDF_device_array_counter].equals("AHD10289DW")) 
											AHD10289DW_Upload(CP_L022_MTK_stdf_folder_content_array[k]);
										if(L022_STDF_device_array[L022_STDF_device_array_counter].equals("AHB10289BW")) 
											AHB10289BW_Upload(CP_L022_MTK_stdf_folder_content_array[k]);
										//Revise code. 20170214-----End
									}
									cmdStr = "AZA10176A_java_Regular_Expression.csh " + "/dx_summary/CP/L022_STDF/" + L022_STDF_device_array[L022_STDF_device_array_counter] + "/ " +
											CP_L022_MTK_stdf_folder_content_array[k] + " " + "/dx_summary/CP/L022_STDF/" + L022_STDF_device_array[L022_STDF_device_array_counter] + "/backup/";
									javaExecSystemCmd2(cmdStr,5000);
									
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

					File CP_L129_MTK_stdf_folder_content_array_file = new File("/dx_summary/CP/L129_STDF/" + L129_STDF_device_array[L129_STDF_device_array_counter]);
					if (CP_L129_MTK_stdf_folder_content_array_file.isDirectory()){//check folder's std file			            						

						CP_L129_MTK_stdf_folder_content_array = CP_L129_MTK_stdf_folder_content_array_file.list();

						if (CP_L129_MTK_stdf_folder_content_array.length != 0){

						//L129 STDF Transfer to Tango(Tango will upload to global foundries server) by Cola-----Start
////							for(int d1 = 0 ; d1 < UploadSTDFtoTango_Device.length ; d1 ++){
////								if(L129_STDF_device_array[L129_STDF_device_array_counter].indexOf(UploadSTDFtoTango_Device[d1]) != -1){
//									SourcePath = MTK_L129_STDF_file + "/" + L129_STDF_device_array[L129_STDF_device_array_counter] +"/";
//									UploadPath = "/testerdata/L129/STDF/SGH/CP/";
//
//									String[] GlobalFoundries_STDF_upload = {"192.168.1.21","loader","loader",SourcePath,UploadPath,".std.gz"};
//									TransferSTDFtoTangoServer(GlobalFoundries_STDF_upload); //Upload to Tango
////								}
////							}
						//L129 STDF Transfer to Tango(Tango will upload to global foundries server) by Cola-----End	
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
										cmdStr = "chmod 777 " + "/dx_summary/CP/L129_STDF/" + L129_STDF_device_array[L129_STDF_device_array_counter] + "/backup";
										javaExecSystemCmd2(cmdStr,1000);
									}
									if(CP_L129_MTK_stdf_folder_content_array[k].indexOf("-CORR") == -1){ //don't send CORR data. 20170306
										//L129 All STDF Transfer to Tango(Tango will upload to global foundries server) by Cola. 20170306-----Start
										SourcePath = MTK_L129_STDF_file + "/" + L129_STDF_device_array[L129_STDF_device_array_counter] +"/";
										UploadPath = "/testerdata/L129/STDF/SGH/CP/";
//										System.out.println("SourcePath: "+SourcePath+" ,UploadPath: "+UploadPath);
										String[] STDF_upload = {"192.168.1.21","loader","loader",SourcePath,UploadPath,".std.gz"};

										cmdStr = "AZA10176A_ftp_sigurd_cp_all.csh";
										for (int i = 0; i < 5; i++)        	
											cmdStr = cmdStr + " " + STDF_upload[i];						

										cmdStr = cmdStr + " " + CP_L129_MTK_stdf_folder_content_array[k] ;
										javaExecSystemCmd2(cmdStr,15000);
										//L129 All STDF Transfer to Tango(Tango will upload to global foundries server) by Cola. 20170306-----End
									/* Cancel Send to OT+ , by Tango upload. 20170621 
										cmdStr = "AZA10176A_ftp_sigurd_cp_all.csh";
										for (int i = 0; i < 5; i++){   	
											if (i != 3){
												cmdStr = cmdStr + " " + L129_MTK_STDF_upload[i];
											}else{
												cmdStr = cmdStr + " " + "/dx_summary/CP/L129_STDF/" + L129_STDF_device_array[L129_STDF_device_array_counter];
											}
										}											
										cmdStr = cmdStr + " " + CP_L129_MTK_stdf_folder_content_array[k] ;
										javaExecSystemCmd2(cmdStr,10000);
									*/
										if(L129_STDF_device_array[L129_STDF_device_array_counter].indexOf("BJC10299B") != -1) //20170214
											BJC10299B_Upload(CP_L129_MTK_stdf_folder_content_array[k]);
									}
									cmdStr = "AZA10176A_java_Regular_Expression.csh " + "/dx_summary/CP/L129_STDF/" + L129_STDF_device_array[L129_STDF_device_array_counter] + "/ " +
											CP_L129_MTK_stdf_folder_content_array[k] + " " + "/dx_summary/CP/L129_STDF/" + L129_STDF_device_array[L129_STDF_device_array_counter] + "/backup/";
									javaExecSystemCmd2(cmdStr,5000); 

								}
							}
						}else{
							System.out.println("NO file......waitting......");
						}
					}
				}
			}				
			//=================L022 L129 STDF upload to OP+======================end
/* Remark 20170214-----Start
			//=================AZA10176A STDF upload to O+ server======================start
			//AZA10176A_STDF_upload_Oplus = {"192.168.1.70","sghpe2","sgh555pe2","/dx_summary/CP/L022_STDF/AZA10176A/","/L022/Oplus/",".std.gz"};		
			File CP_AZA10176A_stdf_folder_content_array_file_Oplus = new File("/dx_summary/CP/L022_STDF/AZA10176A/");
			if (CP_AZA10176A_stdf_folder_content_array_file_Oplus.isDirectory()){//check folder's std file			            						

				CP_AZA10176A_stdf_folder_content_array_Oplus = CP_AZA10176A_stdf_folder_content_array_file_Oplus.list();

				if (CP_AZA10176A_stdf_folder_content_array_Oplus.length != 0){

					if (CP_AZA10176A_stdf_folder_content_array_Oplus.length > 15){
						forloopNO = 15;
					}else{
						forloopNO = CP_AZA10176A_stdf_folder_content_array_Oplus.length;
					}

					AZA10176A_STDF_upload_Oplus[4] = "/L022/Oplus/";
					TaiwanTime = nowdate.format(new java.util.Date());
					System.out.println(TaiwanTime);	
					AZA10176A_STDF_upload_Oplus[4] = AZA10176A_STDF_upload_Oplus[4] + TaiwanTime + "/";
					System.out.println(AZA10176A_STDF_upload_Oplus[4]);

					for (int k = 0; k < forloopNO; k++){
						if(CP_AZA10176A_stdf_folder_content_array_Oplus[k].indexOf(".std.gz") != -1)
							check_CP_stdf_content_flag = true;
						else 
							check_CP_stdf_content_flag = false;
						
						if (check_CP_stdf_content_flag == true){				            										            											

							cmdStr = "AZA10176A_ftp_sigurd_cp_all.csh";
							for (int i = 0; i < 5; i++)        	
								cmdStr = cmdStr + " " + AZA10176A_STDF_upload_Oplus[i];						

							cmdStr = cmdStr + " " + CP_AZA10176A_stdf_folder_content_array_Oplus[k] ;
							javaExecSystemCmd2(cmdStr,10000);

							cmdStr = "AZA10176A_java_Regular_Expression.csh " + AZA10176A_STDF_upload_Oplus[3] + " " +
									CP_AZA10176A_stdf_folder_content_array_Oplus[k] + " " + "/dx_summary/CP/L022_STDF/AZA10176A/backup/";
							javaExecSystemCmd2(cmdStr,10000); 
						}
					}
				}else{
					System.out.println("NO file......waitting......");
				}
			}		
			//=================AZA10176A STDF upload to O+ server======================end		

			//=================AHH10176C STDF upload to O+ server======================start
			//AHH10176C_STDF_upload_Oplus = {"192.168.1.70","sghpe2","sgh555pe2","/dx_summary/CP/L022_STDF/AHH10176C/","/L022/Oplus/",".std.gz"};		
			File CP_AHH10176C_stdf_folder_content_array_file_Oplus = new File("/dx_summary/CP/L022_STDF/AHH10176C/");
			if (CP_AHH10176C_stdf_folder_content_array_file_Oplus.isDirectory()){//check folder's std file			            						

				CP_AHH10176C_stdf_folder_content_array_Oplus = CP_AHH10176C_stdf_folder_content_array_file_Oplus.list();

				if (CP_AHH10176C_stdf_folder_content_array_Oplus.length != 0){

					if (CP_AHH10176C_stdf_folder_content_array_Oplus.length > 15){
						forloopNO = 15;
					}else{
						forloopNO = CP_AHH10176C_stdf_folder_content_array_Oplus.length;
					}

					AHH10176C_STDF_upload_Oplus[4] = "/L022/Oplus/";
					TaiwanTime = nowdate.format(new java.util.Date());
					System.out.println(TaiwanTime);	
					AHH10176C_STDF_upload_Oplus[4] = AHH10176C_STDF_upload_Oplus[4] + TaiwanTime + "/";
					System.out.println(AHH10176C_STDF_upload_Oplus[4]);

					for (int k = 0; k < forloopNO; k++){
						check_CP_AHH10176C_stdf_content_flag = false;
						if(CP_AHH10176C_stdf_folder_content_array_Oplus[k].indexOf(".std.gz") != -1)
							check_CP_AHH10176C_stdf_content_flag = true;

						if (check_CP_AHH10176C_stdf_content_flag == true){				            										            											

							cmdStr = "AZA10176A_ftp_sigurd_cp_all.csh";
							for (int i = 0; i < 5; i++)        	
								cmdStr = cmdStr + " " + AHH10176C_STDF_upload_Oplus[i];						

							cmdStr = cmdStr + " " + CP_AHH10176C_stdf_folder_content_array_Oplus[k] ;
							javaExecSystemCmd2(cmdStr,10000);

							cmdStr = "AZA10176A_java_Regular_Expression.csh " + AHH10176C_STDF_upload_Oplus[3] + " " +
									CP_AHH10176C_stdf_folder_content_array_Oplus[k] + " " + "/dx_summary/CP/L022_STDF/AHH10176C/backup/";
							javaExecSystemCmd2(cmdStr,10000); 
						}
					}
				}else{
					System.out.println("NO file......waitting......");
				}
			}		
			//=================AHH10176C STDF upload to O+ server======================end			

			//=================BJC10299B STDF upload to O+ server======================start
			//BJC10299B_STDF_upload_Oplus = {"192.168.1.70","sghpe2","sgh555pe2","/dx_summary/CP/L022_STDF/BJC10299B/","/L022/Oplus/",".std.gz"};		
			File CP_BJC10299B_stdf_folder_content_array_file_Oplus = new File("/dx_summary/CP/L129_STDF/BJC10299B/");
			if (CP_BJC10299B_stdf_folder_content_array_file_Oplus.isDirectory()){//check folder's std file			            						

				CP_BJC10299B_stdf_folder_content_array_Oplus = CP_BJC10299B_stdf_folder_content_array_file_Oplus.list();

				if (CP_BJC10299B_stdf_folder_content_array_Oplus.length != 0){

					if (CP_BJC10299B_stdf_folder_content_array_Oplus.length > 15){
						forloopNO = 15;
					}else{
						forloopNO = CP_BJC10299B_stdf_folder_content_array_Oplus.length;
					}

					BJC10299B_STDF_upload_Oplus[4] = "/L022/Oplus/";
					TaiwanTime = nowdate.format(new java.util.Date());
					System.out.println(TaiwanTime);	
					BJC10299B_STDF_upload_Oplus[4] = BJC10299B_STDF_upload_Oplus[4] + TaiwanTime + "/";
					System.out.println(BJC10299B_STDF_upload_Oplus[4]);

					for (int k = 0; k < forloopNO; k++){
						check_CP_BJC10299B_stdf_content_flag = false;
						if(CP_BJC10299B_stdf_folder_content_array_Oplus[k].indexOf(".std.gz") != -1)
							check_CP_BJC10299B_stdf_content_flag = true;

						if (check_CP_BJC10299B_stdf_content_flag == true){				            										            											

							cmdStr = "AZA10176A_ftp_sigurd_cp_all.csh";
							for (int i = 0; i < 5; i++)        	
								cmdStr = cmdStr + " " + BJC10299B_STDF_upload_Oplus[i];						

							cmdStr = cmdStr + " " + CP_BJC10299B_stdf_folder_content_array_Oplus[k] ;
							javaExecSystemCmd2(cmdStr,10000);

							cmdStr = "AZA10176A_java_Regular_Expression.csh " + BJC10299B_STDF_upload_Oplus[3] + " " +
									CP_BJC10299B_stdf_folder_content_array_Oplus[k] + " " + "/dx_summary/CP/L129_STDF/BJC10299B/backup/";
							javaExecSystemCmd2(cmdStr,10000); 
						}
					}
				}else{
					System.out.println("NO file......waitting......");
				}
			}		
			//=================BJC10299B STDF upload to O+ server======================end		

			//=================10238 STDF upload======================start
			//BHD10238_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/dx_summary/CP/L022_STDF/BHD10238CW/","/L022/TMFQ18_BHD10238/",".std.gz"};
			//TSMC_BHD10238_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/dx_summary/CP/L022_STDF/BHD10238CW/","/L022/TSMC_BHD10238/",".std.gz"};

			File CP_BHD10238_stdf_folder_content_array_file = new File("/dx_summary/CP/L022_STDF/BHD10238CW/");
			if (CP_BHD10238_stdf_folder_content_array_file.isDirectory()){//check folder's std file				            							

				CP_BHD10238_stdf_folder_content_array = CP_BHD10238_stdf_folder_content_array_file.list();	
				for (int k = 0; k < CP_BHD10238_stdf_folder_content_array.length; k++){
					check_CP_BHD10238_stdf_content_flag = false;
					if(CP_BHD10238_stdf_folder_content_array[k].indexOf(".std.gz") != -1)
						check_CP_BHD10238_stdf_content_flag = true;

					if (check_CP_BHD10238_stdf_content_flag == true){				            										            						

						cmdStr = "java_Regular_Expression.csh " + BHD10238_STDF_upload[3] + " " + "EQC " + BHD10238_STDF_upload[3] + "backup";
						javaExecSystemCmd2(cmdStr,3000);								

						cmdStr = "ftp_sigurd_cp_all.csh";
						for (int i = 0; i < BHD10238_STDF_upload.length; i++)        	
							cmdStr = cmdStr + " " + BHD10238_STDF_upload[i];						

						javaExecSystemCmd2(cmdStr,10000);

						cmdStr = "ftp_sigurd_cp_all.csh";
						for (int i = 0; i < TSMC_BHD10238_STDF_upload .length; i++)     //20150305 autoSent to TSMC by ChiaHui    	
							cmdStr = cmdStr + " " + TSMC_BHD10238_STDF_upload [i];						

						javaExecSystemCmd2(cmdStr,10000);			        							        

						cmdStr = "java_Regular_Expression.csh " + BHD10238_STDF_upload[3] + " " + BHD10238_STDF_upload[5] + " " + "/dx_summary/CP/L022_STDF/BHD10238CW/backup/";
						javaExecSystemCmd2(cmdStr,5000); 
						break;
					}
				}
			}

			//=================10289 STDF upload======================start
			//AHD10289_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/dx_summary/CP/L022_STDF/AHD10289DW/","/L022/TMFJ04_AHD10289/",".std.gz"};
			//TSMC_AHD10289_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/dx_summary/CP/L022_STDF/AHD10289DW/","/L022/TSMC_AHD10289/",".std.gz"};
			//SSMC_AHD10289_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/dx_summary/CP/L022_STDF/AHD10289DW/","/L022/SSMC_AHD10289/",".std.gz"};

			File CP_AHD10289_stdf_folder_content_array_file = new File("/dx_summary/CP/L022_STDF/AHD10289DW/");
			if (CP_AHD10289_stdf_folder_content_array_file.isDirectory()){//check folder's std file				            							

				CP_AHD10289_stdf_folder_content_array = CP_AHD10289_stdf_folder_content_array_file.list();	
				for (int k = 0; k < CP_AHD10289_stdf_folder_content_array.length; k++){
					check_CP_AHD10289_stdf_content_flag = false;
					if(CP_AHD10289_stdf_folder_content_array[k].indexOf(".std.gz") != -1)
						check_CP_AHD10289_stdf_content_flag = true;

					if (check_CP_AHD10289_stdf_content_flag == true){				            										            						

						cmdStr = "java_Regular_Expression.csh " + AHD10289_STDF_upload[3] + " " + "EQC " + AHD10289_STDF_upload[3] + "backup";
						javaExecSystemCmd2(cmdStr,3000);								

						cmdStr = "ftp_sigurd_cp_all.csh";
						for (int i = 0; i < AHD10289_STDF_upload.length; i++)        	
							cmdStr = cmdStr + " " + AHD10289_STDF_upload[i];						

						javaExecSystemCmd2(cmdStr,5000);

						cmdStr = "ftp_sigurd_cp_all.csh";
						for (int i = 0; i < TSMC_AHD10289_STDF_upload .length; i++)     //20150305 autoSent to TSMC by ChiaHui    	
							cmdStr = cmdStr + " " + TSMC_AHD10289_STDF_upload [i];						

						cmdStr = "ftp_sigurd_cp_all.csh";
						for (int i = 0; i < SSMC_AHD10289_STDF_upload .length; i++)     //20150313 autoSent to SSMC by ChiaHui (Chou_Roy)	
							cmdStr = cmdStr + " " + SSMC_AHD10289_STDF_upload [i];

						javaExecSystemCmd2(cmdStr,5000);
						cmdStr = "java_Regular_Expression.csh " + AHD10289_STDF_upload[3] + " " + AHD10289_STDF_upload[5] + " " + "/dx_summary/CP/L022_STDF/AHD10289DW/backup/";
						javaExecSystemCmd2(cmdStr,5000); 
						break;
					}
				}
			}		
			//=================10289 STDF upload======================end		
			//=================AHB10289BW STDF upload======================start
			//SSMC_AHB10289BW_STDF_upload = {"192.168.1.70","sghpe2","sgh555pe2","/dx_summary/CP/L022_STDF/AHB10289BW/","/L022/SSMC_AHD10289/",".std.gz"};

			File CP_AHB10289BW_stdf_folder_content_array_file = new File("/dx_summary/CP/L022_STDF/AHB10289BW/");
			if (CP_AHB10289BW_stdf_folder_content_array_file.isDirectory()){//check folder's std file				            							

				CP_AHB10289BW_stdf_folder_content_array = CP_AHB10289BW_stdf_folder_content_array_file.list();	
				for (int k = 0; k < CP_AHB10289BW_stdf_folder_content_array.length; k++){
					check_CP_AHB10289BW_stdf_content_flag = false;
					if(CP_AHB10289BW_stdf_folder_content_array[k].indexOf(".std.gz") != -1)
						check_CP_AHB10289BW_stdf_content_flag = true;

					if (check_CP_AHB10289BW_stdf_content_flag == true){				            										            						

						cmdStr = "java_Regular_Expression.csh " + SSMC_AHB10289BW_STDF_upload[3] + " " + "EQC " + SSMC_AHB10289BW_STDF_upload[3] + "backup";
						javaExecSystemCmd2(cmdStr,3000);								

						cmdStr = "ftp_sigurd_cp_all.csh";
						for (int i = 0; i < SSMC_AHB10289BW_STDF_upload .length; i++)     //20150730 autoSent to SSMC by ChiaHui (Chou_Roy)	
							cmdStr = cmdStr + " " + SSMC_AHB10289BW_STDF_upload [i];

						javaExecSystemCmd2(cmdStr,5000);
						cmdStr = "java_Regular_Expression.csh " + SSMC_AHB10289BW_STDF_upload[3] + " " + SSMC_AHB10289BW_STDF_upload[5] + " " + "/dx_summary/CP/L022_STDF/AHB10289BW/backup/";
						javaExecSystemCmd2(cmdStr,5000); 
						break;
					}
				}
			}		
			//=================AHB10289BW STDF upload======================end
Remark 20170214-----End */
			//=================RealTimelog upload======================start
			for(int i = 0; i < UploadRealTimeLog_Device_ForMTK.length; i++){  //by Cola. 2016.06.
				System.out.println("Search Device: " + UploadRealTimeLog_Device_ForMTK[i] + " .....");
				TransferRealTimeLogforHRID(UploadRealTimeLog_Device_ForMTK[i]);
			}		
			//=================RealTimelog upload======================end

/*			
			//=================RealTimelog AN10363C upload======================start
			//String[] L129_AN10363C_realTimeLog_upload = {"192.168.1.76","sghpe2","sgh555pe2","/usr/local/home/vol5/RealTimelog/L129/CP/AN10363/","/HRID/AN10363C",".txt.gz"};
			String[] CP_RealTimelog_AN10363C_folder_content_array;  //check .txt file

			System.out.println("RealTimelog AN10363C upload");
			File CP_RealTimelog_AN10363C_folder_content_array_file = new File("/dx_summary/CP/L129/AN10363C/");
			if (CP_RealTimelog_AN10363C_folder_content_array_file.isDirectory()){//check .txt file 				            						
				CP_RealTimelog_AN10363C_folder_content_array = CP_RealTimelog_AN10363C_folder_content_array_file.list();	
				for (int i = 0; i < CP_RealTimelog_AN10363C_folder_content_array.length; i++){
					if(CP_RealTimelog_AN10363C_folder_content_array[i].indexOf(".txt.gz") != -1){

						cmdStr = "ftp_sigurd_cp_all.csh";
						for (int k = 0; k < L129_AN10363C_realTimeLog_upload.length; k++)        	
							cmdStr = cmdStr + " " + L129_AN10363C_realTimeLog_upload[k];						

						javaExecSystemCmd2(cmdStr,10000);
						cmdStr = "java_Regular_Expression.csh " + L129_AN10363C_realTimeLog_upload[3] + " " + L129_AN10363C_realTimeLog_upload[5] + " " + "/dx_summary/CP/L129/AN10363C/backup/";
						javaExecSystemCmd2(cmdStr,10000); 
					}
				}
			}		
			//=================RealTimelog AN10363C upload======================end
			//=================RealTimelog AHH10335BW upload======================start
			//String[] L129_AHH10335BW_realTimeLog_upload = {"192.168.1.76","sghpe2","sgh555pe2","/dx_summary/CP/L129/AHH10335BW/","/HRID/AHH10335BW/",".txt.gz"};
			String[] CP_RealTimelog_AHH10335BW_folder_content_array;  //check .txt file

			System.out.println("RealTimelog AHH10335BW upload");
			File CP_RealTimelog_AHH10335BW_folder_content_array_file = new File("/dx_summary/CP/L129/AHH10335BW/");
			if (CP_RealTimelog_AHH10335BW_folder_content_array_file.isDirectory()){//check .txt file 				            						
				CP_RealTimelog_AHH10335BW_folder_content_array = CP_RealTimelog_AHH10335BW_folder_content_array_file.list();	
				for (int i = 0; i < CP_RealTimelog_AHH10335BW_folder_content_array.length; i++){
					if(CP_RealTimelog_AHH10335BW_folder_content_array[i].indexOf(".txt.gz") != -1){

						cmdStr = "ftp_sigurd_cp_all.csh";
						for (int k = 0; k < L129_AHH10335BW_realTimeLog_upload.length; k++)        	
							cmdStr = cmdStr + " " + L129_AHH10335BW_realTimeLog_upload[k];						

						javaExecSystemCmd2(cmdStr,10000);
						cmdStr = "java_Regular_Expression.csh " + L129_AHH10335BW_realTimeLog_upload[3] + " " + L129_AHH10335BW_realTimeLog_upload[5] + " " + "/dx_summary/CP/L129/AHH10335BW/backup/";
						javaExecSystemCmd2(cmdStr,10000); 
					}
				}
			}		
			//=================RealTimelog AHH10335BW upload======================end		
			//=================RealTimelog BHH10326CW upload======================start
			//String[] L022_BHH10326CW_realTimeLog_upload = {"192.168.1.76","sghpe2","sgh555pe2","/dx_summary/CP/L022/BHH10326CW/","/HRID/BHH10326CW/",".txt.gz"};
			String[] CP_RealTimelog_BHH10326CW_folder_content_array;  //check .txt file

			System.out.println("RealTimelog BHH10326CW upload");
			File CP_RealTimelog_BHH10326CW_folder_content_array_file = new File("/dx_summary/CP/L022/BHH10326CW/");
			if (CP_RealTimelog_BHH10326CW_folder_content_array_file.isDirectory()){//check .txt file 				            						
				CP_RealTimelog_BHH10326CW_folder_content_array = CP_RealTimelog_BHH10326CW_folder_content_array_file.list();	
				for (int i = 0; i < CP_RealTimelog_BHH10326CW_folder_content_array.length; i++){
					if(CP_RealTimelog_BHH10326CW_folder_content_array[i].indexOf(".txt.gz") != -1){

						cmdStr = "ftp_sigurd_cp_all.csh";
						for (int k = 0; k < L022_BHH10326CW_realTimeLog_upload.length; k++)        	
							cmdStr = cmdStr + " " + L022_BHH10326CW_realTimeLog_upload[k];						

						javaExecSystemCmd2(cmdStr,10000);
						cmdStr = "java_Regular_Expression.csh " + L022_BHH10326CW_realTimeLog_upload[3] + " " + L022_BHH10326CW_realTimeLog_upload[5] + " " + "/dx_summary/CP/L022/BHH10326CW/backup/";
						javaExecSystemCmd2(cmdStr,10000); 
					}
				}
			}		
			//=================RealTimelog BHH10326CW upload======================end	
			//=================RealTimelog AZA10335CW upload======================start 20160420
			//String[] L129_AZA10335CW_realTimeLog_upload = {"192.168.1.76","sghpe2","sgh555pe2","/dx_summary/CP/L129/AZA10335CW/","/HRID/AZA10335CW/",".txt.gz"};
			String[] CP_RealTimelog_AZA10335CW_folder_content_array;  //check .txt file

			System.out.println("RealTimelog AZA10335CW upload");
			File CP_RealTimelog_AZA10335CW_folder_content_array_file = new File("/dx_summary/CP/L129/AZA10335CW/");
			if (CP_RealTimelog_AZA10335CW_folder_content_array_file.isDirectory()){//check .txt file 				            						
				CP_RealTimelog_AZA10335CW_folder_content_array = CP_RealTimelog_AZA10335CW_folder_content_array_file.list();	
				for (int i = 0; i < CP_RealTimelog_AZA10335CW_folder_content_array.length; i++){
					if(CP_RealTimelog_AZA10335CW_folder_content_array[i].indexOf(".txt.gz") != -1){

						cmdStr = "ftp_sigurd_cp_all.csh";
						for (int k = 0; k < L129_AZA10335CW_realTimeLog_upload.length; k++)        	
							cmdStr = cmdStr + " " + L129_AZA10335CW_realTimeLog_upload[k];						

						javaExecSystemCmd2(cmdStr,10000);
						cmdStr = "java_Regular_Expression.csh " + L129_AZA10335CW_realTimeLog_upload[3] + " " + L129_AZA10335CW_realTimeLog_upload[5] + " " + "/dx_summary/CP/L129/AZA10335CW/backup/";
						javaExecSystemCmd2(cmdStr,10000); 
					}
				}
			}		
			//=================RealTimelog AZA10335CW upload======================end
			//=================RealTimelog AZA10333B upload======================start 20160615
			//String[] L129_AZA10335CW_realTimeLog_upload = {"192.168.1.76","sghpe2","sgh555pe2","/dx_summary/CP/L129/AZA10335CW/","/HRID/AZA10335CW/",".txt.gz"};
			String[] CP_RealTimelog_AZA10333B_folder_content_array;  //check .txt file

			System.out.println("RealTimelog AZA10333B upload");
			File CP_RealTimelog_AZA10333B_folder_content_array_file = new File("/dx_summary/CP/L022/AZA10333B/");
			if (CP_RealTimelog_AZA10333B_folder_content_array_file.isDirectory()){//check .txt file 				            						
				CP_RealTimelog_AZA10333B_folder_content_array = CP_RealTimelog_AZA10333B_folder_content_array_file.list();	
				for (int i = 0; i < CP_RealTimelog_AZA10333B_folder_content_array.length; i++){
					if(CP_RealTimelog_AZA10333B_folder_content_array[i].indexOf(".txt.gz") != -1){

						cmdStr = "ftp_sigurd_cp_all.csh";
						for (int k = 0; k < L022_AZA10333B_realTimeLog_upload.length; k++)        	
							cmdStr = cmdStr + " " + L022_AZA10333B_realTimeLog_upload[k];						

						javaExecSystemCmd2(cmdStr,10000);
						cmdStr = "java_Regular_Expression.csh " + L022_AZA10333B_realTimeLog_upload[3] + " " + L022_AZA10333B_realTimeLog_upload[5] + " " + "/dx_summary/CP/L022/AZA10333B/backup/";
						javaExecSystemCmd2(cmdStr,10000); 
					}
				}
			}		
			//=================RealTimelog AZA10335CW upload======================end
*/			
			System.out.println("Waiting for next loop ...");
			Thread.sleep(180000);																											        
		}            
	}


}





//=================L121 STDF TMGG68C-T1EAZT upload to tango server======================start
//String[] L121_TMGG68C_T1EAZT_STDF_upload = {"192.168.1.21","loader","loader","/dx_summary/CP/L121_STDF/TMGG68C_T1EAZT/","/testerdata/L121/STDF/SGH/CP/",".std.gz"};	
/*		File CP_L121_TMGG68C_T1EAZT_stdf_folder_content_array_file = new File(L121_TMGG68C_T1EAZT_STDF_upload[3]);
		if (CP_L121_TMGG68C_T1EAZT_stdf_folder_content_array_file.isDirectory()){//check folder's std file			            						

			CP_L121_TMGG68C_T1EAZT_stdf_folder_content_array = CP_L121_TMGG68C_T1EAZT_stdf_folder_content_array_file.list();

			if (CP_L121_TMGG68C_T1EAZT_stdf_folder_content_array.length != 0){

				if (CP_L121_TMGG68C_T1EAZT_stdf_folder_content_array.length > 15){
					forloopNO = 15;
				}else{
					forloopNO = CP_L121_TMGG68C_T1EAZT_stdf_folder_content_array.length;
				}

				for (int k = 0; k < forloopNO; k++){
					if(CP_L121_TMGG68C_T1EAZT_stdf_folder_content_array[k].indexOf(".std.gz") != -1)
					check_CP_L121_TMGG68C_T1EAZT_stdf_content_flag = true;

					if (check_CP_L121_TMGG68C_T1EAZT_stdf_content_flag == true){				            										            											

						cmdStr = "AZA10176A_ftp_sigurd_cp_all.csh";
						for (int i = 0; i < 5; i++)        	
							cmdStr = cmdStr + " " + L121_TMGG68C_T1EAZT_STDF_upload[i];						

						cmdStr = cmdStr + " " + CP_L121_TMGG68C_T1EAZT_stdf_folder_content_array[k] ;
						javaExecSystemCmd2(cmdStr,10000);

						cmdStr = "AZA10176A_java_Regular_Expression.csh " + L121_TMGG68C_T1EAZT_STDF_upload[3] + " " +
						CP_L121_TMGG68C_T1EAZT_stdf_folder_content_array[k] + " " +
						"/dx_summary/CP/L121_STDF/TMGG68C_T1EAZT/backup/";
						javaExecSystemCmd2(cmdStr,10000); 
					}
				}
			}else{
				System.out.println("NO file......waitting......");
			}
		}	*/	
//=================L121 STDF upload to tango server======================end
//=================L121 STDF TMGG68C-T1EACT upload to tango server======================start
//String[] L121_TMGG68C_T1EACT_STDF_upload = {"192.168.1.21","loader","loader","/dx_summary/CP/L121_STDF/TMGG68C_T1EACT/","/testerdata/L121/STDF/SGH/CP/",".std.gz"};
/*
		File CP_L121_TMGG68C_T1EACT_stdf_folder_content_array_file = new File(L121_TMGG68C_T1EACT_STDF_upload[3]);
		if (CP_L121_TMGG68C_T1EACT_stdf_folder_content_array_file.isDirectory()){//check folder's std file			            						

			CP_L121_TMGG68C_T1EACT_stdf_folder_content_array = CP_L121_TMGG68C_T1EACT_stdf_folder_content_array_file.list();

			if (CP_L121_TMGG68C_T1EACT_stdf_folder_content_array.length != 0){

				if (CP_L121_TMGG68C_T1EACT_stdf_folder_content_array.length > 15){
					forloopNO = 15;
				}else{
					forloopNO = CP_L121_TMGG68C_T1EACT_stdf_folder_content_array.length;
				}

				for (int k = 0; k < forloopNO; k++){
					if(CP_L121_TMGG68C_T1EACT_stdf_folder_content_array[k].indexOf(".std.gz") != -1)
					check_CP_L121_TMGG68C_T1EACT_stdf_content_flag = true;

					if (check_CP_L121_TMGG68C_T1EACT_stdf_content_flag == true){				            										            											

						cmdStr = "AZA10176A_ftp_sigurd_cp_all.csh";
						for (int i = 0; i < 5; i++)        	
							cmdStr = cmdStr + " " + L121_TMGG68C_T1EACT_STDF_upload[i];						

						cmdStr = cmdStr + " " + CP_L121_TMGG68C_T1EACT_stdf_folder_content_array[k] ;
						javaExecSystemCmd2(cmdStr,10000);

						cmdStr = "AZA10176A_java_Regular_Expression.csh " + L121_TMGG68C_T1EACT_STDF_upload[3] + " " +
						CP_L121_TMGG68C_T1EACT_stdf_folder_content_array[k] + " " +
						"/dx_summary/CP/L121_STDF/TMGG68C_T1EACT/backup/";
						javaExecSystemCmd2(cmdStr,10000); 
					}
				}
			}else{
				System.out.println("NO file......waitting......");
			}
		}	*/	
//=================L121 STDF upload to tango server======================end