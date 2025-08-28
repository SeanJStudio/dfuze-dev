/**
 * Project: Dfuze
 * File: SystemDatabase.java
 * Date: Mar 9, 2020
 * Time: 7:06:59 PM
 */
package com.mom.dfuze.db;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mom.dfuze.data.Job;
import com.mom.dfuze.data.JobSorters;
import com.mom.dfuze.data.jobs.abcprinting.ABCPrintingJob;
import com.mom.dfuze.data.jobs.bcaa.BCAAJob;
import com.mom.dfuze.data.jobs.bccancerfoundation.BCCancerFoundationJob;
import com.mom.dfuze.data.jobs.bchydro.BCHydroJob;
import com.mom.dfuze.data.jobs.belairinsurancecompany.BelairInsuranceCompanyJob;
import com.mom.dfuze.data.jobs.calgarydreamcentre.CalgaryDreamCentreJob;
import com.mom.dfuze.data.jobs.canadianceliacassociation.CanadianCeliacAssociationJob;
import com.mom.dfuze.data.jobs.canuckplacechildrenshospice.CanuckPlaceChildrensHospiceJob;
import com.mom.dfuze.data.jobs.connecthearing.ConnectHearingJob;
import com.mom.dfuze.data.jobs.cushmanwakefield.CushmanWakefieldJob;
import com.mom.dfuze.data.jobs.generosityx.GenerosityXJob;
import com.mom.dfuze.data.jobs.gffinancial.GFFinancialJob;
import com.mom.dfuze.data.jobs.harveymckinnonassociates.HarveyMckinnonAssociatesJob;
import com.mom.dfuze.data.jobs.khalsadiwansociety.KhalsaDiwanSocietyJob;
import com.mom.dfuze.data.jobs.kinsmen.KinsmenJob;
import com.mom.dfuze.data.jobs.lunghealthfoundation.LungHealthFoundationJob;
import com.mom.dfuze.data.jobs.missionbonaccuiel.MissionBonAccuielJob;
import com.mom.dfuze.data.jobs.newdemocraticparty.NewDemocraticPartyJob;
import com.mom.dfuze.data.jobs.peacearchhospital.PeaceArchHospitalJob;
import com.mom.dfuze.data.jobs.ridgemeadowshospital.RidgeMeadowsHospitalJob;
import com.mom.dfuze.data.jobs.surreyhospitalfoundation.SurreyHospitalFoundationJob;
import com.mom.dfuze.data.jobs.thecommongood.TheCommonGoodJob;
import com.mom.dfuze.data.jobs.utility.UtilityJob;
import com.mom.dfuze.data.jobs.vghubc.VGHUBCJob;


/**
 * SystemDao Class to function as an internal database to hold all the jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class SystemDao {
  public static Job[] jobs = { 
		  
		  new UtilityJob(new com.mom.dfuze.data.jobs.utility.NonAddressSeparatorTwoLine()),
		  new UtilityJob(new com.mom.dfuze.data.jobs.utility.NonAddressSeparatorThreeLine()),
		  new UtilityJob(new com.mom.dfuze.data.jobs.utility.NonAddressSeparatorFourLine()),
		  new UtilityJob(new com.mom.dfuze.data.jobs.utility.CalculateDistance()),
		  new UtilityJob(new com.mom.dfuze.data.jobs.utility.DataDump()),
		  new UtilityJob(new com.mom.dfuze.data.jobs.utility.DataVerification()),
		  new UtilityJob(new com.mom.dfuze.data.jobs.utility.Dedupe()),
		  new UtilityJob(new com.mom.dfuze.data.jobs.utility.FixApartmentHyphens()),
		  //new UtilityJob(new com.mom.dfuze.data.jobs.utility.Hygiene()),
		  new UtilityJob(new com.mom.dfuze.data.jobs.utility.InferProvince()),
		  new UtilityJob(new com.mom.dfuze.data.jobs.utility.Load()),
		  new UtilityJob(new com.mom.dfuze.data.jobs.utility.Random5050Split()),
		  new UtilityJob(new com.mom.dfuze.data.jobs.utility.Random7525Split()),
		  new UtilityJob(new com.mom.dfuze.data.jobs.utility.RandomUserDefinedSplit()),
		  //new UtilityJob(new com.mom.dfuze.data.jobs.utility.RebelHelper()),
		  new UtilityJob(new com.mom.dfuze.data.jobs.utility.RegisteredMail()),
		  new UtilityJob(new com.mom.dfuze.data.jobs.utility.RemoveBadAddresses()),
		  new UtilityJob(new com.mom.dfuze.data.jobs.utility.RemoveNonLowerMainland()),  
		  new UtilityJob(new com.mom.dfuze.data.jobs.utility.Rental()),
		  new UtilityJob(new com.mom.dfuze.data.jobs.utility.Parcel()),
		  new UtilityJob(new com.mom.dfuze.data.jobs.utility.ParseCityProvPC()),
		  new UtilityJob(new com.mom.dfuze.data.jobs.utility.SplitAddressLines()),
		  //new UtilityJob(new com.mom.dfuze.data.jobs.utility.Test()),
		  //new UtilityJob(new com.mom.dfuze.data.jobs.utility.SplitName()),
		  //new UtilityJob(new com.mom.dfuze.data.jobs.utility.CleanPSMFiles()),
		  
		  //A
		  new ABCPrintingJob(new com.mom.dfuze.data.jobs.abcprinting.ReformatAddress()),
		  
		  //B
		  new BelairInsuranceCompanyJob(new com.mom.dfuze.data.jobs.belairinsurancecompany.RegularProcess()),
		  new BCAAJob(new com.mom.dfuze.data.jobs.bcaa.Tai()),
		  // new BCAAJob(new com.mom.dfuze.data.jobs.bcaa.PotSweetner()),
		  
		  new BCCancerFoundationJob(new com.mom.dfuze.data.jobs.bccancerfoundation.RegularProcess()),
		  
		  new BCHydroJob(new com.mom.dfuze.data.jobs.bchydro.PON()),
		  
		  //C
		  new CalgaryDreamCentreJob(new com.mom.dfuze.data.jobs.calgarydreamcentre.RegularProcess()),
		  
		  new CanadianCeliacAssociationJob(new com.mom.dfuze.data.jobs.canadianceliacassociation.RegularProcess()),
		  
		  new CanuckPlaceChildrensHospiceJob(new com.mom.dfuze.data.jobs.canuckplacechildrenshospice.RegularProcess()),
		  //new CanuckPlaceChildrensHospiceJob(new com.mom.dfuze.data.jobs.canuckplacechildrenshospice.NoSegmentationRegularProcess()),
		  
		  new ConnectHearingJob(new com.mom.dfuze.data.jobs.connecthearing.NonWeeklyStep1()),
		  new ConnectHearingJob(new com.mom.dfuze.data.jobs.connecthearing.NonWeeklyStep2()),
		  
		  new CushmanWakefieldJob(new com.mom.dfuze.data.jobs.cushmanwakefield.SplitNameAddress()),
		  
		  //G
		  new GenerosityXJob(new com.mom.dfuze.data.jobs.generosityx.Adra()),
		  new GenerosityXJob(new com.mom.dfuze.data.jobs.generosityx.AdraUkraine()),
		  new GenerosityXJob(new com.mom.dfuze.data.jobs.generosityx.CaminoWellbeingMentalHealth()),
		  new GenerosityXJob(new com.mom.dfuze.data.jobs.generosityx.IntervalHouseOttawa()),
		  new GenerosityXJob(new com.mom.dfuze.data.jobs.generosityx.InternationalTeamsCanada()),
		  new GenerosityXJob(new com.mom.dfuze.data.jobs.generosityx.ItIsWritten()),
		  new GenerosityXJob(new com.mom.dfuze.data.jobs.generosityx.LarcheComplex()),
		  new GenerosityXJob(new com.mom.dfuze.data.jobs.generosityx.LarcheSimple()),
		  new GenerosityXJob(new com.mom.dfuze.data.jobs.generosityx.MBA()),
		  new GenerosityXJob(new com.mom.dfuze.data.jobs.generosityx.P2C()),
		  new GenerosityXJob(new com.mom.dfuze.data.jobs.generosityx.UnfoldingWord()),
          new GenerosityXJob(new com.mom.dfuze.data.jobs.generosityx.WildlifeRescueAssociation()),
          
		  new GFFinancialJob(new com.mom.dfuze.data.jobs.gffinancial.VRM()),
		  
		  
		  //H
		  new HarveyMckinnonAssociatesJob(new com.mom.dfuze.data.jobs.harveymckinnonassociates.BCChildrensHospitalFoundation()),
		  new HarveyMckinnonAssociatesJob(new com.mom.dfuze.data.jobs.harveymckinnonassociates.CanadianCentrePolicyAlternatives()),
		  new HarveyMckinnonAssociatesJob(new com.mom.dfuze.data.jobs.harveymckinnonassociates.CentralOkanaganFoodBank()),
          new HarveyMckinnonAssociatesJob(new com.mom.dfuze.data.jobs.harveymckinnonassociates.ChildrensHospitalFoundationOfManitoba()),
          new HarveyMckinnonAssociatesJob(new com.mom.dfuze.data.jobs.harveymckinnonassociates.CovenantHouse()),
          new HarveyMckinnonAssociatesJob(new com.mom.dfuze.data.jobs.harveymckinnonassociates.EdmontonHumanSociety()),
          new HarveyMckinnonAssociatesJob(new com.mom.dfuze.data.jobs.harveymckinnonassociates.GreaterVancouverFoodBank()),
          new HarveyMckinnonAssociatesJob(new com.mom.dfuze.data.jobs.harveymckinnonassociates.LionsFoundationDogGuides()),
          new HarveyMckinnonAssociatesJob(new com.mom.dfuze.data.jobs.harveymckinnonassociates.MarmotRecoveryFoundation()),
          new HarveyMckinnonAssociatesJob(new com.mom.dfuze.data.jobs.harveymckinnonassociates.MustardSeedVictoria()),
          //new HarveyMckinnonAssociatesJob(new com.mom.dfuze.data.jobs.harveymckinnonassociates.SimonFraserUniversity()),
          new HarveyMckinnonAssociatesJob(new com.mom.dfuze.data.jobs.harveymckinnonassociates.SimonFraserUniversity2()),
          new HarveyMckinnonAssociatesJob(new com.mom.dfuze.data.jobs.harveymckinnonassociates.SpecialOlympicsCanada()),
          
          //K
          new KhalsaDiwanSocietyJob(new com.mom.dfuze.data.jobs.khalsadiwansociety.RegularProcess()),
          new KinsmenJob(new com.mom.dfuze.data.jobs.kinsmen.RegularProcess()),

          //L
          new LungHealthFoundationJob(new com.mom.dfuze.data.jobs.lunghealthfoundation.RegularProcess()),
          //new LungHealthFoundationJob(new com.mom.dfuze.data.jobs.lunghealthfoundation.CPLookup()),
          
          //M
          new MissionBonAccuielJob(new com.mom.dfuze.data.jobs.missionbonaccuiel.RegularProcess()),
          
          //N
          new NewDemocraticPartyJob(new com.mom.dfuze.data.jobs.newdemocraticparty.RegularProcess()),
          
          //O
          
          //P
          new PeaceArchHospitalJob(new com.mom.dfuze.data.jobs.peacearchhospital.RegularProcess()),
          
          //R
          //new RitchieBrothersJob(new com.mom.dfuze.data.jobs.ritchiebrothers.RegularProcess()),
          //new RitchieBrothersJob(new com.mom.dfuze.data.jobs.ritchiebrothers.MakeInkjetFileForBuhrs()),
          new RidgeMeadowsHospitalJob(new com.mom.dfuze.data.jobs.ridgemeadowshospital.RegularProcess()),
          
          //S
          new SurreyHospitalFoundationJob(new com.mom.dfuze.data.jobs.surreyhospitalfoundation.RegularProcess()),
          
          //T
          //new ThinkTechnicaJob(new com.mom.dfuze.data.jobs.thinktechnica.RegularProcess()),
          new TheCommonGoodJob(new com.mom.dfuze.data.jobs.thecommongood.FarmRadio()),
          
          //V
          new VGHUBCJob(new com.mom.dfuze.data.jobs.vghubc.RegularProcess()),
          new VGHUBCJob(new com.mom.dfuze.data.jobs.vghubc.SplitAddress()),
          
          //W

          };

  private static Set<Job> jobSet = new HashSet<>(Arrays.asList(jobs));
  private Set<String> clientNameSet = new HashSet<>();

  /**
   * Constructor for objects of Class SystemDao
   */
  public SystemDao() {
    setClientNameSet(clientNameSet);
  }

  /**
   * Gets the jobs from the SystemDao
   * 
   * @return the SystemDao jobs as a Job[]
   */
  public static Job[] getJobs() {
    List<Job> jobsList = Arrays.asList(jobs);
    Collections.sort(jobsList, new JobSorters.CompareName());
    return jobsList.toArray(new Job[jobsList.size()]);
  }

  /**
   * Gets the SystemDao jobSet
   * 
   * @return the SystemDao jobSet as a Set<Job>
   */
  public Set<Job> getJobSet() {
    return jobSet;
  }

  /**
   * Sets the unique clientNames from the SystemDao JobSet
   * 
   * @param clientNameSet
   *          the set of ClientNames to set
   */
  public void setClientNameSet(Set<String> clientNameSet) {

    for (Job job : jobSet) {
      if (!clientNameSet.contains(job.getClientName())) {
        clientNameSet.add(job.getClientName());
      }
    }

  }

  public Set<String> getClientNameSet() {
    return clientNameSet;
  }

}
