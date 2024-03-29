import urllib.request
import urllib.error
import shutil
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
    'SFE': 'Santa Fe',
    'RKM': 'Rockingham Park',
    'RUI': 'Ruidoso Downs',
    'RUP': 'Rupert Downs',
    'SAC': 'Sacremento',
    'HOU': 'Houston Race Park',
    'SLR': 'San Luis Rey Training Center',
    'SDY': 'Sandy Downs',
    'SA': 'Santa Anita Park',
    'SON': 'Santa Cruz County Fair @ Sonoita',
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
racepdfs_location = 'C:/Users/JDesktop/Desktop/PDFSFINAL6.2.18/chunk4/'


def main():

    curr_trackname = ''
    start_date = date(1991, 1, 1)
    end_date = date(2018, 5, 20)

    for shortcode, track_name in current_chunk.items():
        print('Starting Track: ' + track_name)
        if curr_trackname != track_name:
            curr_trackname = track_name
            if(not os.path.exists(racepdfs_location +
                                  curr_trackname + '/')):
                print('Making New Directory For: ' + track_name)
                os.mkdir(racepdfs_location + curr_trackname
                         + '/', 0o755)

        curr_year = 0

        for single_date in date_range(start_date, end_date):
            year = single_date.strftime("%Y")
            if curr_year < int(year):
                if(not os.path.exists(racepdfs_location +
                                      curr_trackname + '/' + str(year) + '/')):
                    print('Making New Directory For: ' + track_name + ' and year: ' + year)
                    os.mkdir(racepdfs_location +
                             curr_trackname + '/' + str(year) + '/', 0o755)
                curr_year = int(year)

            url = 'http://www.equibase.com/premium/eqbPDFChartPlus.cfm?RACE=A&BorP=P&TID=' + shortcode + '&CTRY=' +\
                  country_code + '&DT=' + single_date.strftime("%m/%d/%Y") + '&DAY=D&STYLE=EQB'
            hdr = {'User-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chro'
                                 'me/66.0.3359.181 Safari/537.36',
                   'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8',
                   'Accept-Encoding': 'gzip, deflate',
                   'Accept-Language': 'en-US,en;q=0.9',
                   'Connection': 'keep-alive',
                   'DNT': '1',
                   'Host': 'www.equibase.com',
                   'Upgrade-Insecure-Requests': '1',
                   'Cookie': 'hasLiveRampMatch=true; dt=Sun Jun 03 2018; EQBHOME=2; _ga=GA1.2.1405624710.1525628357; __qca=P0-601022164-1525628358120; __gads=ID=6aefe9a7c278b1fb:T=1525628362:S=ALNI_MazBZt4GM1-A7BDiIiC04AiOeejLg; clickandchat.com=855-1525628377653; __sonar=9148314863431239511; D_SID=98.10.201.79:NcnZqG2bv6xFPms/SWenPL2zQZcGJT4to7xMSkKpivU; __unam=802c0e5-163478e0a9f-67384ed6-2; OX_plg=pm; COOKIE_TEST=TEST; __utma=118826909.792118245.1526837255.1526837255.1526837255.1; __utmc=118826909; __utmz=118826909.1526837255.1.1.utmccn=(direct)|utmcsr=(direct)|utmcmd=(none); hasLiveRampMatch=true; _gid=GA1.2.2133650322.1527975522; dt=Sat Jun 02 2018; SAP=0206182354TN; D_IID=BD6911C7-1DCB-3E00-80A9-71BEEDD3C119; D_UID=CE8BF1C1-479B-381F-B29B-C4F61A5483F6; D_ZID=0082D931-0301-39C4-A15E-5AA140C83452; D_ZUID=48BC915B-ADF5-346C-ABCA-51C60418FBF0; D_HID=D1D3C3E3-2C18-3DAB-BA6B-3946AD618FCD; dpm_url_count=61; dpm_time_site=57596.912000000055'
                   }
            req = urllib.request.Request(url, headers=hdr)
            file_name = racepdfs_location + curr_trackname \
                        + '/' + str(year) + '/' + single_date.strftime("%m%d%y") + '.pdf'
            if not os.path.exists(file_name):
                try:
                    with urllib.request.urlopen(req) as response:
                        with open(file_name, 'wb') as out_file:
                            shutil.copyfileobj(response, out_file)
                except urllib.error.HTTPError as err:
                    if err.code == 404:
                        print("404")
                        print('Lost: ' + single_date.strftime("%m/%d/%Y" + ' for track: ' + track_name))
                    else:
                        print(err)
                        print('Lost: ' + single_date.strftime("%m/%d/%Y" + ' for track: ' + track_name))
            else:
                pass
        print('All days checked for this track!')
    print('All Tracks have been completed.')


def date_range(start_date, end_date):
    for n in range(int((end_date - start_date).days)):
        yield start_date + timedelta(n)


main()
