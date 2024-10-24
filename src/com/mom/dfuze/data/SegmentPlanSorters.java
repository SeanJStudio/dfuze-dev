/**
 * Project: Dfuze
 * File: SegmentPlanSorters.java
 * Date: Jun 20, 2020
 * Time: 9:10:40 PM
 */
package com.mom.dfuze.data;

import java.util.Comparator;

/**
 * Collection of Inner Classes to compare and sort SegmentPlans
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class SegmentPlanSorters {

	/**
	 * Inner Class to compare SegmentPlans by their from gift date in descending order
	 */
	public static class CompareByPriority implements Comparator<SegmentPlan> {

		/*
		 * (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(SegmentPlan s1, SegmentPlan s2) {
			int priority = Double.compare(s1.getPriority(), s2.getPriority());

			if (priority == 0)
				return 0;
			else
				return priority;

		}

	}

	public static class CompareByPriorityAscendingDescriptionDescening implements Comparator<SegmentPlan> {

		/*
		 * (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(SegmentPlan s1, SegmentPlan s2) {
			int priority = Double.compare(s1.getPriority(), s2.getPriority());
			int segmentDescription = s2.getOneAndAllCCMSegmentDescription().compareTo(s1.getOneAndAllCCMSegmentDescription());

			if (priority == 0)
				if(segmentDescription == 0)
					return 0;
				else
					return segmentDescription;
			else
				return priority;

		}

	}


	public static class CompareBySegCodeAscending implements Comparator<SegmentPlan> {

		/*
		 * (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(SegmentPlan s1, SegmentPlan s2) {
			int segCode = s1.getSegmentCode().compareTo(s2.getSegmentCode());

			if (segCode == 0)
				return 0;
			else
				return segCode;

		}

	}

	public static class CompareBySegCodeDescending implements Comparator<SegmentPlan> {

		/*
		 * (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(SegmentPlan s1, SegmentPlan s2) {
			int segCode = s2.getSegmentCode().compareTo(s1.getSegmentCode());

			if (segCode == 0)
				return 0;
			else
				return segCode;

		}

	}

	public static class CompareByOneAndAllCCMSegmentDescriptionDescending implements Comparator<SegmentPlan> {

		/*
		 * (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(SegmentPlan s1, SegmentPlan s2) {
			int segCode = s2.getOneAndAllCCMSegmentDescription().compareTo(s1.getOneAndAllCCMSegmentDescription());

			if (segCode == 0)
				return 0;
			else
				return segCode;

		}

	}



	/**
	 * Inner Class to compare SegmentPlans by their from gift date in descending order
	 */
	public static class CompareByFilter1Filter2FromGiftDateDescending implements Comparator<SegmentPlan> {

		/*
		 * (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(SegmentPlan s1, SegmentPlan s2) {
			int filter1 = s2.getSegmentPlanFilter1().compareTo(s1.getSegmentPlanFilter1());
			int filter2 = s2.getSegmentPlanFilter2().compareTo(s1.getSegmentPlanFilter2());
			int giftDates = s2.getFromGiftDate().compareTo(s1.getFromGiftDate());

			if (filter1 == 0)
				if (filter2 == 0)
					if (giftDates == 0)
						return 0;
					else
						return giftDates;
				else
					return filter2;
			else
				return filter1;
		}

	}
	
	/**
	 * Inner Class to compare SegmentPlans by their from gift date in descending order
	 */
	public static class CompareByFilter1Filter2PriorityFromGiftDateDescending implements Comparator<SegmentPlan> {

		/*
		 * (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(SegmentPlan s1, SegmentPlan s2) {
			int filter1 = s2.getSegmentPlanFilter1().compareTo(s1.getSegmentPlanFilter1());
			int filter2 = s2.getSegmentPlanFilter2().compareTo(s1.getSegmentPlanFilter2());
			int priority = Double.compare(s1.getPriority(), s2.getPriority());
			int giftDates = s2.getFromGiftDate().compareTo(s1.getFromGiftDate());

			if (filter1 == 0)
				if (filter2 == 0)
					if (priority == 0)
						if (giftDates == 0)
							return 0;
						else
							return giftDates;
						else
							return priority;
					else
						return filter2;
				else
					return filter1;
		}

	}

}
