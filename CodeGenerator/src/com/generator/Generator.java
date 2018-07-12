package com.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;

public class Generator {
	public static enum CodeParamType{
		ParamType_Void,
		ParamType_int,
		ParamType_Float,
		ParamType_String
	}
	
	public static class CodeParam{
		public CodeParamType paramType;
		public String paramName;
	}
	
	public static class CodeFunction{
		public CodeParamType retType;
		public String funcName;
		public ArrayList<CodeParam> params;
		
		public CodeFunction()
		{
			params = new ArrayList<CodeParam>();
		}
	}
	
	public static class CodeFile{
		public String fileName;
		public String copyFuncName;
		public String copyParamName;
		public ArrayList<CodeFunction> funcs;
		
		public CodeFile(){
			funcs = new ArrayList<CodeFunction>();
		}
	}
	
	private static String[] alpha = new String[]{"A","B","C", "D", "E","F", "G", "H", "I", "J", "K", "L", "M", "N", "O","P","Q","R","S","T","U","V","W","X","Y","Z",
		"a","b","c", "d", "e","f", "g", "h", "i", "j", "k", "l", "m", "n", "o","p","q","r","s","t","u","v","w","x","y","z"};
	private static String[] numeric = new String[]{"0","1","2","3","4","5","6","7","8","9","0"};
	private static String underline = "_";
	private static Random rand = new Random();
	private static ArrayList<String> nameList = new ArrayList<String>();
	
	private static String generateFileName(){
		int len = 8 + rand.nextInt(8);
		StringBuffer buff = new StringBuffer();
		for(int i = 0; i < len; i++){
			buff.append(alpha[rand.nextInt(alpha.length)]);
		}
		return buff.toString();
	}
	
	private static String generateFuncName(){
		int len = 6 + rand.nextInt(8);
		StringBuffer buff = new StringBuffer();
		buff.append(underline);
		buff.append(alpha[rand.nextInt(alpha.length)]);
		for(int i = 2; i < len; i++){
			int index = rand.nextInt(alpha.length + numeric.length);
			if(index < alpha.length){
				buff.append(alpha[index]);
			} else {
				buff.append(numeric[index - alpha.length]);
			}
		}
		return buff.toString();
	}
	
	private static String generateVarName(){
		int len = 6 + rand.nextInt(4);
		StringBuffer buff = new StringBuffer();
		buff.append(alpha[rand.nextInt(alpha.length)]);
		for(int i = 1; i < len; i++){
			int index = rand.nextInt(alpha.length + numeric.length);
			if(index < alpha.length){
				buff.append(alpha[index]);
			} else {
				buff.append(numeric[index - alpha.length]);
			}
		}
		return buff.toString();
	}
	
	private static CodeParamType generateRetType(){
		int ret = rand.nextInt(4);
		return CodeParamType.values()[ret];
	}
	
	private static CodeParamType generateParamType(){
		int ret = rand.nextInt(3);
		return CodeParamType.values()[1 + ret];
	}
	
	private static CodeFunction generateFunc(String funcName){
		CodeFunction func = new CodeFunction();
		func.funcName = funcName;
		func.retType = generateRetType();
		int cnt = 0;
		if(func.retType == CodeParamType.ParamType_int || func.retType == CodeParamType.ParamType_Float){
			cnt = 2 + rand.nextInt(3);
		} else {
			cnt = rand.nextInt(4);
		}
		ArrayList<String> list = new ArrayList<String>();
		while(list.size() < cnt){
			String paramName = generateVarName();
			if(!list.contains(paramName) && !paramName.equals(funcName)){
				list.add(paramName);
				CodeParam param = new CodeParam();
				param.paramName = paramName;
				if(func.retType == CodeParamType.ParamType_int || func.retType == CodeParamType.ParamType_Float){
					param.paramType = func.retType;
				} else {
					param.paramType = generateParamType();	
				}
				func.params.add(param);
			}
		}
		return func;
	}
	
	private static BufferedWriter getBufferedWriter(String path, String fileName, String extend){
		try {
			if(!path.endsWith("/")){
				path = path + "/";
			}
			File file = new File(path);    
			if(!file .exists() && !file .isDirectory())      
			{       
			    file .mkdir();    
			}			
			FileWriter fileWriter = new FileWriter(path + fileName + extend, false);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			return bufferedWriter;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static String retTypeToString(CodeParamType type)
	{
		switch(type){
		case ParamType_Void:
			return "void";
		case ParamType_int:
			return "int";
		case ParamType_Float:
			return "float";
		case ParamType_String:
			return "const char*";
		default:
			return "void";
		}
	}
	
	private static String paramTypeToString(CodeParamType type)
	{
		switch(type){
		case ParamType_Void:
			return "void";
		case ParamType_int:
			return "int";
		case ParamType_Float:
			return "float";
		case ParamType_String:
			return "char*";
		default:
			return "void";
		}
	}
	
	private static void printFuncHeader(BufferedWriter buffer, CodeFunction func)
	{
		try
		{
			buffer.write("extern ");
			buffer.write(retTypeToString(func.retType) + " ");
			buffer.write(func.funcName);
			buffer.write("(");
			for(int i= 0; i < func.params.size(); i++){
				CodeParam param = func.params.get(i);
				buffer.write(paramTypeToString(param.paramType) + " ");
				buffer.write(param.paramName);
				if(i != func.params.size() - 1){
					buffer.write(", ");
				}
			}
			buffer.write(")");
			buffer.write(";\n");
			buffer.write("\n");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static void closeWriter(BufferedWriter buffer){
		try
		{
			buffer.close();
		}
		catch(Exception e1)
		{
			e1.printStackTrace();
		}	
	}
	
	private static void printHeaderFile(String path, CodeFile codeFile)
	{
		BufferedWriter buffer = getBufferedWriter(path, codeFile.fileName, ".h");
		try
		{
			buffer.write("#ifndef " + codeFile.fileName + "_h\n");
			buffer.write("#define " + codeFile.fileName + "_h\n");
			buffer.write("\n");
			for(CodeFunction codeFunc: codeFile.funcs){
				printFuncHeader(buffer, codeFunc);
			}
			buffer.write("#endif");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		} finally
		{
			closeWriter(buffer);
		}
	}
	
	private static void printCopyFunc(BufferedWriter buffer, String funcName, String paramName)
	{
		try{
			String tempName = "";
			while(true){
				tempName = generateVarName();
				if(!tempName.equals(funcName) && !tempName.equals(paramName)){
					break;
				}
			}
			buffer.write("char* ");
			buffer.write(funcName);
			buffer.write("(");
			buffer.write("const char* ");
			buffer.write(paramName);
			buffer.write(")\n");
			buffer.write("{\n");
			buffer.write("    if (" + paramName + " == NULL)\n");
			buffer.write("        return NULL;\n");
			buffer.write("\n");
			buffer.write("    char* " + tempName + " = (char*)malloc(strlen(" + paramName + ") + 1);\n");
			buffer.write("    strcpy(" + tempName + " , " + paramName + ");\n");
			buffer.write("    return " + tempName + ";\n");
			buffer.write("}\n");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static String typePrint(CodeParamType type){
		switch(type){
		case ParamType_int:
			return "d";
		case ParamType_Float:
			return "f";
		default:
			return "@";	
		}
	}
	
	private static String printParamVal(CodeParam param){
		switch(param.paramType){
		case ParamType_String:
			return "[NSString stringWithUTF8String:" + param.paramName + "]";
		default:
			return param.paramName;
		}
	}
	
	private static String generateSign(){
		int index = rand.nextInt(4);
		switch(index)
		{
		case 0:
			return " - ";
		case 1:
			return " * ";
		case 2:
			return " / ";
		default:
			return " + ";
		}
	}
	
	private static String generateRandInt(){
		return String.valueOf(rand.nextInt(100000));
	}
	
	private static String generateRandStr(){
		int len = 6 + rand.nextInt(20);
		StringBuffer buff = new StringBuffer();
		for(int i = 0; i < len; i++){
			int index = rand.nextInt(alpha.length + numeric.length);
			if(index < alpha.length){
				buff.append(alpha[index]);
			} else {
				buff.append(numeric[index - alpha.length]);
			}
		}
		return buff.toString();
	}
	
	private static void printFunc(BufferedWriter buffer, CodeFunction func, String copyName){
		try
		{
			buffer.write(retTypeToString(func.retType) + " ");
			buffer.write(func.funcName);
			buffer.write("(");
			for(int i = 0; i < func.params.size(); i++){
				CodeParam codeParam = func.params.get(i);
				buffer.write(paramTypeToString(codeParam.paramType) + " " + codeParam.paramName);
				if(i != func.params.size() - 1)
				{
					buffer.write(", ");
				}
			}
			buffer.write(")\n");
			buffer.write("{\n");
			for(CodeParam param: func.params){
				buffer.write("    NSLog(@\"%@=%" + typePrint(param.paramType) + "\", @\"" + param.paramName + "\", " + printParamVal(param) + ");\n");
			}
			if(func.retType != CodeParamType.ParamType_Void){
				buffer.write("\n");
				buffer.write("    return ");
				if(func.retType != CodeParamType.ParamType_String){
					for(int i = 0; i < func.params.size(); i++){
						buffer.write(func.params.get(i).paramName);
						if(i != func.params.size() - 1)
						{
							buffer.write(generateSign());
						}
					}
				} else {
					buffer.write(copyName + "(");
					if(func.params.size() > 0){
						buffer.write("[[NSString stringWithFormat:@\"");
						for(CodeParam param: func.params){
							buffer.write("%" + typePrint(param.paramType));
						}
						buffer.write("\"");
						for(CodeParam param: func.params){
							buffer.write(", " + printParamVal(param));
						}
						buffer.write("] UTF8String]");
					} else {
						String str = generateRandStr();
						buffer.write("\"" + str + "\"");
					}
					buffer.write(")");
				}
				buffer.write(";\n");
			}
			buffer.write("}\n");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static void printMFile(String path, CodeFile codeFile)
	{
		BufferedWriter buffer = getBufferedWriter(path, codeFile.fileName, ".m");
		try
		{
			buffer.write("#import \"" + codeFile.fileName + ".h\"\n");
			buffer.write("\n");
			printCopyFunc(buffer, codeFile.copyFuncName, codeFile.copyParamName);
			buffer.write("\n");
			for(CodeFunction func: codeFile.funcs){
				printFunc(buffer, func, codeFile.copyFuncName);
				buffer.write("\n");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		} finally
		{
			closeWriter(buffer);
		}
	}
	
	private static void printCode(String path, CodeFile codeFile){
		printHeaderFile(path, codeFile);
		printMFile(path, codeFile);
	}
	
	private static void generateFileCode(String path, String fileName, ArrayList<CodeFile> codes){
		CodeFile codeFile = new CodeFile();
		codes.add(codeFile);
		codeFile.fileName = fileName;
		while(true){
			String copyFuncName = generateFuncName();
			if(!nameList.contains(copyFuncName)){
				codeFile.copyFuncName = copyFuncName;
				nameList.add(copyFuncName);
				break;
			}
		}
		codeFile.copyParamName = generateVarName();
		ArrayList<String> list = new ArrayList<String>();
		int cnt = 80 + rand.nextInt(80);
		while(list.size() < cnt){
			String funcName = generateFuncName();
			if(!nameList.contains(funcName)){
				nameList.add(funcName);
				list.add(funcName);
			}
		}
		for(String funcName: list){
			codeFile.funcs.add(generateFunc(funcName));
		}
		printCode(path, codeFile);
	}
	
	private static String generateRandParam(CodeParam param){
		switch(param.paramType){
		case ParamType_String:
			return "\"" + generateRandStr() + "\"";
		case ParamType_int:
			return  String.valueOf(generateRandInt());
		case ParamType_Float:
			return String.valueOf(generateRandInt());
		default:
			return "";	
		}
	}
	
	private static void printCallFunc(BufferedWriter buff, CodeFunction func){
		try
		{
			if(func.retType == CodeParamType.ParamType_Void){
				buff.write("    " + func.funcName + "(");
				for(int i = 0; i < func.params.size(); i++){
					buff.write(generateRandParam(func.params.get(i)));
					if(i != func.params.size() - 1){
						buff.write(", ");
					}
				}
				buff.write(");\n");
			} else {
				buff.write("    NSLog(@\""+ func.funcName + "=%"+ typePrint(func.retType) + "\",");
				if(func.retType == CodeParamType.ParamType_String){
					buff.write("[NSString stringWithUTF8String:");
				}
				buff.write(func.funcName + "(");
				for(int i = 0; i < func.params.size(); i++){
					buff.write(generateRandParam(func.params.get(i)));
					if(i != func.params.size() - 1){
						buff.write(", ");
					}
				}
				buff.write(")");
				if(func.retType == CodeParamType.ParamType_String){
					buff.write("]");
				}
				buff.write(");\n");	
			}	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static void printCallCode(String path, ArrayList<CodeFile> codeList){
		BufferedWriter headBuff = getBufferedWriter(path, "InitCaller", ".h");
		try
		{
			headBuff.write("#ifndef InitCaller_h\n");
			headBuff.write("#define InitCaller_h\n");
			headBuff.write("\n");
			headBuff.write("extern void _callFunc();\n");
			headBuff.write("\n");
			headBuff.write("#endif");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		} finally
		{
			closeWriter(headBuff);
		}
	
		BufferedWriter mBuff = getBufferedWriter(path, "InitCaller", ".m");
		try
		{
			mBuff.write("#import \"InitCaller.h\"\n");
			for(CodeFile file: codeList){
				mBuff.write("#import \""+ file.fileName + ".h\"\n");
			}
			mBuff.write("\n");
			mBuff.write("void _callFunc()\n");
			mBuff.write("{\n");
			for(CodeFile file: codeList){
				for(CodeFunction func: file.funcs){
					printCallFunc(mBuff, func);
				}
			}
			mBuff.write("}\n");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		} finally
		{
			closeWriter(mBuff);
		}
	}
	
	public static void generateCode(String path, int fileCount){
		nameList.clear();
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<CodeFile> codes = new ArrayList<CodeFile>();
		while(list.size() < fileCount){
			String fileName = generateFileName();
			if(!nameList.contains(fileName)){
				nameList.add(fileName);
				list.add(fileName);
			}
		}
		for(String fileName: list){
			generateFileCode(path, fileName, codes);
		}
		
		printCallCode(path, codes);
	}
	
	public static void main(String[] args){
		generateCode("/Users/Ampaw/Desktop/Code/", 20);
	}
}
