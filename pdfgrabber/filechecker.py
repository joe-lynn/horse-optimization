from datetime import timedelta, date
import os

can_tracks = {
    'AJX': 'Ajax Downs',
    'ASD': 'Assiniboia Downs',
    'BNR': 'Bar None Ranches',
    'CTD': 'Century Downs',
    'DEP': 'Desert Park',
    'FE ': 'Fort Erie',
    'GPR': 'Grand Prairie',
    'HST': 'Hastings Racecourse',
    'KAM': 'Kamloops',
    'KIN': 'Kin Park',
    'LBG': 'Lethbridge',
    'NP': 'Northlands Park',
    'MIL': 'Millarville',
    'MD': 'Marquis Downs',
    'MDA': 'Melville District Agripar',
    'PIC': 'Picov Downs',
    'RPD': 'Rossburn Parkland Downs',
    'SND': 'Sunflower',
    'STP': 'Stampede Park',
    'WO': 'Woodbine',
}

pr_tracks = {
    'CMR': 'Camarero Race Track',
}

chunk1 = {
    'AIK': 'Aiken',
    'ALB': 'Albuquerque',
    'AQA': 'American Quarter Horse Association',
    'ANF': 'Anthony Downs',
    'AQU': 'Aqueduct',
    'ARP': 'Arapahoe Park',
    'ARC': 'Arc De Triomphe France',
    'AP': 'Arlington Park',
    'ATH': 'Atlanta',
    'ATL': 'Atlantic City',
    'ATO': 'Atokad Downs',
    'BTP': 'Balterra Park',
    'BSR': 'Barretts Race Meet At Fairplex',
    'BM': 'Bay Meadows',
    'BMF': 'Bay Meadows Fair',
    'BEL': 'Belmont Park',
    'BHP': 'Betfair Hollywood Park',
    'BEU': 'Beulah Park',
    'BKF': 'Black Foot',
    'BRD': 'Blue Ribbon Downs',
    'BOW': 'Bowie',
    'BRO': 'Brookhill Farm',
    'BCF': 'Brown County Fair',
    'CRC': 'Calder Race Course',
    'CAM': 'Camden',
    'CBY': 'Canterbury Park',
    'CAS': 'Cassia County Fair',
    'CWF': 'Central Wyoming Fair',
    'CHL': 'Charlotte',
    'CPW': 'Chippewa Downs',
    'CD': 'Churchill Downs',
    'CLM': 'Clemmons',
    'S13': 'Club Hipico Concepcion',
    'DG ': 'Cochise County Fair @ Douglas',
    'CNL': 'Colonial Downs',
    'CLS': 'Columbus',
    'PRV': 'Crooked River Roundup',
    'DAY': 'Dayton',
    'DMR': 'Del Mar',
    'DEL': 'Delaware Park',
    'DED': 'Delta Downs',
    'DXD': 'Dixie Downs',
    'DBA': 'Dual Breed A',
    'DBB': 'Dual Breed B',
    'DBC': 'Dual Breed C',
    'UN ': 'Easter Oregon Livestock Show',
    'ELK': 'Elko County Fair',
    'ELP': 'Ellis Park',
    'EMD': 'Emerald Downs',
    'EMT': 'Emmett',
}

chunk2 = {
    'EUR': 'Eureka',
    'EVD': 'Evangeline Downs',
    'FG ': 'Fair Grounds',
    'FAI': 'Fair Hill',
    'FMT': 'Fair Meadows',
    'FPL': 'Fair Play Park',
    'FAX': 'Fairfax',
    'FP ': 'Fairmount Park',
    'FPX': 'Fairplex Park',
    'FH ': 'Far Hills',
    'FER': 'Ferndale',
    'FL ': 'Finger Lakes',
    'FON': 'Fonner Park',
    'FTP': 'Fort Pierre',
    'FX ': 'Foxfield',
    'FNO': 'Fresno',
    'S14': 'Gavea Brazil',
    'GV ': 'Genesee Valley',
    'GIL': 'Gillespie County Fairground',
    'MID': 'Glenwood Park at Middleburg',
    'GLN': 'Glyndon',
    'GG ': 'Golden Gate Fields',
    'SAF': 'Graham County Fair @ Safford',
    'GN ': 'Grand National',
    'GRP': 'Grants Pass',
    'GBF': 'Great Barrington Fair',
    'GF ': 'Great Falls',
    'GLD': 'Great Lakes Downs',
    'GRM': 'Great Meadow',
    'N11': 'Great Meadow NSA',
    'DUN': 'Greenelee County Fair @ Duncan',
    'GP ': 'Gulfstream Park',
    'GPW': 'Gulfstream Park West',
    'BRN': 'Harney County Fair',
    'HAW': 'Hawthorne',
    'HP': 'Hazel Park',
    'HIA': 'Hialeah Park',
    'CT': 'Hollywood Casino at Charles Town Races',
    'HOL': 'Hollywood Park',
    'HOO': 'Hoosier Park',
    'HPO': 'Horsemen\'s Park',
    'HCF': 'Humboldt County Fair',
    'IND': 'Indiana Grand Race Course',
    'JRM': 'Jerome County Fair',
    'KSP': 'Kalispell',
    'KEE': 'Keeneland',
    'KD': 'Kentucky Downs',
    'NS0': 'Kentucky Downs NSA',
    'LAM': 'La Mesa Park',
    'LBT': 'Laurel Brown Racetrack',
}

chunk3 = {
    'LRL': 'Laurel Park',
    'BOI': 'Les Bois Park',
    'LEX': 'Lexington',
    'LNN': 'Lincoln Race Course',
    'LEV': 'Little Everglades',
    'LS': 'Lone Star Park',
    'LA': 'Los Alamitos',
    'LRC': 'Los Alamitos Race Course',
    'LAD': 'Louisiana Downs',
    'LQB': 'LQHBA Louisiana Quarter Horse Association',
    'MVR': 'Mahoning Valley Race Course',
    'MAL': 'Malvern',
    'MAN': 'Manor Downs',
    'MAF': 'Marias Fair',
    'MED': 'Meadowlands',
    'MC': 'Miles City',
    'MS': 'Mill Spring',
    'MON': 'Monkton',
    'MTH': 'Monmouth Park',
    'MOR': 'Morevn Park',
    'MNR': 'Mountaineer Casino Racetrack & Resort',
    'MPM': 'Mt. Pleasant Meadows',
    'FAR': 'North Dakota Horse Park',
    'NVD': 'Northville Downs',
    'OKR': 'Oak Ridge',
    'OTH': 'Oak Tree at Hollywood Park',
    'OP': 'Oaklawn Park',
    'OSA': 'Oal Tree at Santa Anita',
    'OTP': 'Oat Tree at Pleasanton',
    'OTC': 'Ocala Training Center',
    'ONE': 'Oneida County Fair',
    'PMB': 'Palm Beach Polo Club',
    'PRX': 'Parx Racing',
    'Pen': 'Penn National',
    'UNI': 'Pensylvania Hunt Cup',
    'PW': 'Percy Warner',
    'PHA': 'Philadelpha Park',
    'PIM': 'Pimlico',
    'PNL': 'Pinnacle Race Course',
    'PLN': 'Pleasanton',
    'POD': 'Pocatello Downs',
    'PM': 'Portland Meadows',
    'PRM': 'Prairie Meadows',
    'PID': 'Presque Isle Downs',
    'RB': 'Red Bank',
    'RDM': 'Red Mile',
    'RP': 'Remington Park',
    'RET': 'Retama Park',
    'RIL': 'Rillito',
    'RD': 'River Downs',

}

chunk4 = {
    'RKM': 'Rockingham Park',
    'RUI': 'Ruidoso Downs',
    'RUP': 'Rupert Downs',
    'SAC': 'Sacremento',
    'HOU': 'Houston Race Park',
    'SLR': 'San Luis Rey Training Center',
    'SDY': 'Sandy Downs',
    'SA': 'Santa Anita Park',
    'SON': 'Santa Cruz County Fair @ Sonoita',
    'SFE': 'Santa Fe',
    'SR': 'Santa Rosa',
    'SHW': 'Shawan Downs',
    'SOL': 'Solano',
    'SPT': 'Sportsman\'s Park',
    'STK': 'Stockton',
    'STN': 'Stoneybrook at Five Points',
    'SH': 'Strawberry Hill',
    'SUF': 'Suffolk Downs',
    'SUD': 'Sun Downs',
    'SUN': 'Sunland Park',
    'SRP': 'Sunray Park',
    'SWF': 'Sweetwater Downs',
    'TAM': 'Tampa Bay Downs',
    'WDS': 'The Woodlands',
    'TDN': 'Thistledown',
    'TIL': 'Tillamook County Fair',
    'TIM': 'Timonium',
    'TGD': 'Tioga Downs',
    'TRY': 'Tryon',
    'TUP': 'Turf Paradise',
    'TP': 'Turfway Park',
    'WTS': 'Waistburg Race Track',
    'WW': 'Walla Walla',
    'WBR': 'Weber Downs',
    'WMF': 'Western Mt Fair',
    'ELY': 'White Pine Racing',
    'WRD': 'Will Rogers Downs',
    'WIL': 'Willowdale Steeplechase',
    'WNT': 'Winterthur',
    'WYO': 'Wyoming Downs',
    'YM': 'Yakima Meadows',
    'YAV': 'Yavapai Downs',
    'YD': 'Yellowstone Downs',
    'ZIA': 'Zia Park'
}

current_chunk = chunk4
country_code = 'USA'
racepdfs_location = 'C:/Users/JDesktop/Desktop/PDFSFINAL6.2.18/chunk4'


def main():
    start_date = date(1991, 1, 1)
    end_date = date(2018, 5, 20)
    for shortcode, track_name in current_chunk.items():

        for single_date in date_range(start_date, end_date):
            year = single_date.strftime("%Y")
            day_formatted = single_date.strftime("%m%d%y")
            url = racepdfs_location + '/' + track_name + '/' + year + '/' + day_formatted + '.pdf'
            curr_size = os.path.getsize(url)
            if curr_size >= 2000 and curr_size <= 3000:
                print('possible bad - 2  - ' + url)
            elif curr_size < 600:
                pass
                #print('between 2 and 16 = ' + url)
            else:
                pass
                #print(curr_size)


def date_range(start_date, end_date):
    for n in range(int((end_date - start_date).days)):
        yield start_date + timedelta(n)


main()
