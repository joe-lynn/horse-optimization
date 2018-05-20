import urllib.request
import urllib.error
import shutil


def main():
    url = 'http://www.equibase.com/premium/eqbPDFChartPlus.cfm?RACE=A&BorP=P&TID=AIK&CTRY=USA&DT=03/23/1991&DAY=D&STY' \
          'LE=EQB'
    file_name = 'test.pdf'
    try:
        hdr = {'User-agent': 'Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5'}

        req = urllib.request.Request(url, headers=hdr)
        with urllib.request.urlopen(req) as response, open(file_name, 'wb') as out_file:
            shutil.copyfileobj(response, out_file)
        print(url)
    except urllib.error.HTTPError as err:
        if err.code == 404:
            print('404')
        else:
            print(err)


main()
