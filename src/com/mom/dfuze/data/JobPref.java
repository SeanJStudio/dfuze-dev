package com.mom.dfuze.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.io.AccessReader;
import com.mom.dfuze.io.AccessWriter;

public class JobPref {

	private static final File JOB_PREFS_FILE = new File("jobs/job_prefs.mdb");
	private static final File JOB_PREFS_TEMP_FILE = new File("jobs/temp_job_prefs.mdb");
	private static final String JOB_PREFS_REMEMBER_TABLE = "jobRememberFields";

	private static final int JOB_PREFS_CLIENT_INDEX = 0;
	private static final int JOB_PREFS_JOB_INDEX = 1;
	private static final int JOB_PREFS_REMEMBER_INDEX = 2;

	private static final int JOB_PREFS_FIELD_INDEX = 0;
	private static final int JOB_PREFS_FIELD_VALUE_INDEX = 1;

	private static final String CLIENT_HEADER = "CLIENT";
	private static final String JOB_HEADER = "JOB";
	private static final String REMEMBER_HEADER = "REMEMBER";
	private static final String FIELD_HEADER = "FIELD";
	private static final String VALUE_HEADER = "VALUE";

	public static final String[] jobRememberFieldsHeaders = {CLIENT_HEADER, JOB_HEADER, REMEMBER_HEADER};
	public static final String[] jobLastUsedFieldsHeaders = {FIELD_HEADER, VALUE_HEADER};

	private JobPref() {}

	public static HashMap<String, String> getLastUsedFieldValues(JobPrefRemember jpr) throws ApplicationException {
		try {
			String[][] data = AccessReader.readAsStringArray(JOB_PREFS_FILE, jpr.getKey());
			HashMap<String, String> lastUsedFields = new HashMap<>();

			for(String[] row : data) {
				lastUsedFields.put(row[JOB_PREFS_FIELD_INDEX], row[JOB_PREFS_FIELD_VALUE_INDEX]);
				//LastUsedField luf = new LastUsedField(row[JOB_PREFS_FIELD_INDEX], row[JOB_PREFS_FIELD_VALUE_INDEX]);
			}

			return lastUsedFields;

		}catch(Exception e) {
			throw new ApplicationException(e);
		}
	}

	public static void updateLastUsedFieldValues(Job job, ArrayList<LastUsedField> lufList) throws ApplicationException {
		try{
			String jobTable = job.getClientName()+job.getRunBehavior().getRunBehaviorName();

			String[][] data = new String[lufList.size()][jobLastUsedFieldsHeaders.length];

			int counter = 0;
			for(LastUsedField luf : lufList) {
				data[counter][JOB_PREFS_FIELD_INDEX] = luf.getLastUsedField();
				data[counter][JOB_PREFS_FIELD_VALUE_INDEX] = luf.getLastUsedFieldValue();
				++counter;
			}

			AccessWriter.dropTable(JOB_PREFS_FILE, jobTable);
			AccessWriter.writeTableToDatabase255(JOB_PREFS_FILE, jobLastUsedFieldsHeaders, data, jobTable);
		}catch(Exception e) {
			throw new ApplicationException(e);
		}
	}

	public static void initDatabase(Job[] jobs) throws ApplicationException {
		try {
			if(AccessWriter.createDatabase(JOB_PREFS_FILE)) {

				ArrayList<JobPrefRemember> jprList = new ArrayList<>();

				for(Job job : jobs)
					jprList.add(new JobPrefRemember(job.getClientName(), job.getRunBehavior().getRunBehaviorName(), JobPrefRemember.REMEMBER_FALSE));

				String[][] data = new String[jprList.size()][jobRememberFieldsHeaders.length];

				int counter = 0;
				for(JobPrefRemember tempJpr : jprList) {
					data[counter][JOB_PREFS_CLIENT_INDEX] = tempJpr.getClient();
					data[counter][JOB_PREFS_JOB_INDEX] = tempJpr.getJob();
					data[counter][JOB_PREFS_REMEMBER_INDEX] = (tempJpr.isRemember()) ? JobPrefRemember.REMEMBER_TRUE : JobPrefRemember.REMEMBER_FALSE;
					++counter;
				}

				AccessWriter.writeTableToDatabase255(JOB_PREFS_FILE, jobRememberFieldsHeaders, data, JOB_PREFS_REMEMBER_TABLE);
			}
		}catch(Exception e) {
			throw new ApplicationException(e);
		}
	}

	public static JobPrefRemember getJobPrefRemember(Job job) throws ApplicationException {
		ArrayList<JobPrefRemember> jprList = getJobPrefRememberList();

		for(JobPrefRemember jpr : jprList) {
			System.out.println(jpr.getKey());
			if(jpr.getClient().equalsIgnoreCase(job.getClientName()))
				if(jpr.getJob().equalsIgnoreCase(job.getRunBehavior().getRunBehaviorName()))
					return jpr;
		}

		throw new ApplicationException("Could not find jobPrefRemember");

	}

	public static ArrayList<JobPrefRemember> getJobPrefRememberList() throws ApplicationException{
		System.out.println("Getting JobPrefRememberList");
		ArrayList<JobPrefRemember> list = new ArrayList<>();

		String[][] data = AccessReader.readAsStringArray(JOB_PREFS_FILE, JOB_PREFS_REMEMBER_TABLE);

		for(String[] row : data) {
			String prefClient = row[JOB_PREFS_CLIENT_INDEX];
			String prefJob = row[JOB_PREFS_JOB_INDEX];
			String prefRemember = row[JOB_PREFS_REMEMBER_INDEX];

			JobPrefRemember jpr = new JobPrefRemember(prefClient, prefJob, prefRemember);

			list.add(jpr);
		}

		return list;
	}

	public static void initializeJobPrefRemember(Job[] jobs) throws Exception {
		System.out.println("initializing JobPrefRemember");
		HashSet<String> jobSet = new HashSet<>();

		for(Job job : jobs)
			jobSet.add(job.getClientName()+job.getRunBehavior().getRunBehaviorName());

		ArrayList<JobPrefRemember> jprList = getJobPrefRememberList();

		for(int i = jprList.size() - 1; i >= 0; --i) {
			JobPrefRemember tempJpr = jprList.get(i);
			if(!jobSet.contains(tempJpr.getKey()))
				jprList.remove(i);
		}

		HashSet<String> jprSet = new HashSet<>();

		for(JobPrefRemember tempJpr : jprList)
			jprSet.add(tempJpr.getKey());

		for(Job job : jobs)
			if(!jprSet.contains(job.getClientName()+job.getRunBehavior().getRunBehaviorName()))
				jprList.add(new JobPrefRemember(job.getClientName(), job.getRunBehavior().getRunBehaviorName(), JobPrefRemember.REMEMBER_FALSE));

		String[][] data = new String[jprList.size()][3];

		int counter = 0;
		for(JobPrefRemember tempJpr : jprList) {
			data[counter][JOB_PREFS_CLIENT_INDEX] = tempJpr.getClient();
			data[counter][JOB_PREFS_JOB_INDEX] = tempJpr.getJob();
			data[counter][JOB_PREFS_REMEMBER_INDEX] = (tempJpr.isRemember()) ? JobPrefRemember.REMEMBER_TRUE : JobPrefRemember.REMEMBER_FALSE;
			++counter;
		}

		for(String[] row : data) {
			System.out.println(Arrays.asList(row));
		}

		AccessWriter.dropTable(JOB_PREFS_FILE, JOB_PREFS_REMEMBER_TABLE);
		AccessWriter.writeTableToDatabase255(JOB_PREFS_FILE, jobRememberFieldsHeaders, data, JOB_PREFS_REMEMBER_TABLE);
	}

	public static void updateJobPrefRemember(JobPrefRemember jpr) throws Exception {
		System.out.println("updating JobPrefRemember");
		ArrayList<JobPrefRemember> jprList = getJobPrefRememberList();

		for(JobPrefRemember tempJpr : jprList) {
			if(jpr.getClient().equalsIgnoreCase(tempJpr.getClient()))
				if(jpr.getJob().equalsIgnoreCase(tempJpr.getJob())) {
					tempJpr.setRemember(jpr.isRemember());
					break;
				}
		}

		String[][] data = new String[jprList.size()][3];

		int counter = 0;
		for(JobPrefRemember tempJpr : jprList) {
			data[counter][JOB_PREFS_CLIENT_INDEX] = tempJpr.getClient();
			data[counter][JOB_PREFS_JOB_INDEX] = tempJpr.getJob();
			data[counter][JOB_PREFS_REMEMBER_INDEX] = (tempJpr.isRemember()) ? JobPrefRemember.REMEMBER_TRUE : JobPrefRemember.REMEMBER_FALSE;
			++counter;
		}

		AccessWriter.dropTable(JOB_PREFS_FILE, JOB_PREFS_REMEMBER_TABLE);
		AccessWriter.writeTableToDatabase255(JOB_PREFS_FILE, jobRememberFieldsHeaders, data, JOB_PREFS_REMEMBER_TABLE);
	}

	public static void buildJobPrefTables(Job[] jobs) throws Exception {
		System.out.println("building jobPrefTables");
		String[] tableNames = AccessReader.readTableNames(JOB_PREFS_FILE);
		HashSet<String> tableSet = new HashSet<>();
		for(String table : tableNames)
			tableSet.add(table);

		for(Job job : jobs) {
			String jobTableName = job.getClientName()+job.getRunBehavior().getRunBehaviorName();
			if(!tableSet.contains(jobTableName)) {
				String[] fields = job.getRunBehavior().getRequiredFields();
				String[][] data = new String[fields.length][jobLastUsedFieldsHeaders.length];

				int counter = -1;
				for(String[] row : data) {
					row[JOB_PREFS_FIELD_INDEX] = fields[++counter];
					row[JOB_PREFS_FIELD_VALUE_INDEX] = "";
				}

				// Need to remove table here
				
				AccessWriter.writeTableToDatabase255(JOB_PREFS_FILE, jobLastUsedFieldsHeaders, data, jobTableName);
			}

		}
	}

	public static void validateJobPrefTableFields(Job[] jobs) throws Exception {
		System.out.println("validating jobPrefTableFields");
		for(Job job : jobs) {
			String jobTableName = job.getClientName()+job.getRunBehavior().getRunBehaviorName();
			String[] fields = job.getRunBehavior().getRequiredFields();
			HashSet<String> fieldSet = new HashSet<>();

			for(String field : fields)
				fieldSet.add(field);

			String[][] data = AccessReader.readAsStringArray(JOB_PREFS_FILE, jobTableName);


			for(String[] row : data) {
				String dbField = row[JOB_PREFS_FIELD_INDEX];
				if(!fieldSet.contains(dbField)) {
					String[][] newData = new String[fields.length][jobLastUsedFieldsHeaders.length];

					int counter = -1;
					for(String[] newRow : newData) {
						newRow[JOB_PREFS_FIELD_INDEX] = fields[++counter];
						newRow[JOB_PREFS_FIELD_VALUE_INDEX] = "";
					}
					
					AccessWriter.dropTable(JOB_PREFS_FILE, jobTableName);
					AccessWriter.writeTableToDatabase255(JOB_PREFS_FILE, jobLastUsedFieldsHeaders, data, jobTableName);
					break;
				}
			}
		}
	}

	public static void cleanupJobPrefTableFields(Job[] jobs) throws Exception {
		System.out.println("cleaning up jobPrefTableFields");
		String[] tableNames = AccessReader.readTableNames(JOB_PREFS_FILE);
		HashSet<String> tableSet = new HashSet<>();

		for(String table : tableNames)
			tableSet.add(table);

		HashSet<String> jobSet = new HashSet<>();

		for(Job job : jobs) {
			String jobTableName = job.getClientName()+job.getRunBehavior().getRunBehaviorName();
			jobSet.add(jobTableName);
		}

		jobSet.add(JOB_PREFS_REMEMBER_TABLE); // important to keep this table

		for(String tempTable : tableSet)
			if(!jobSet.contains(tempTable))
				AccessWriter.dropTable(JOB_PREFS_FILE, tempTable);

	}

	public static void buildNewJobPrefDb() throws ApplicationException {
		AccessWriter.makeCopy(JOB_PREFS_FILE, JOB_PREFS_TEMP_FILE);
		JOB_PREFS_FILE.delete();
		JOB_PREFS_TEMP_FILE.renameTo(JOB_PREFS_FILE);
	}

	public static void setup(Job[] jobs) throws Exception {
		initDatabase(jobs); // ensure the jobprefs database exists
		initializeJobPrefRemember(jobs); // adds jobs and removes jobs that dont exist
		buildJobPrefTables(jobs); // builds the job pref tables
		validateJobPrefTableFields(jobs); // ensures all fields needed are in the table
		cleanupJobPrefTableFields(jobs); // removes unused tables
		buildNewJobPrefDb(); // since the file size increases, make a copy and delete the old one
	}

}
