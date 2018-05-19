import urllib.request
import urllib.error
import shutil
from datetime import timedelta, date


def main():

    start_date = date(2018, 1, 1)
    end_date = date(2018, 6, 1)
    for single_date in date_range(start_date, end_date):
        url = 'http://www.equibase.com/static/chart/pdf/GP' + single_date.strftime("%m%d%y") + 'USA' + '.pdf'
        file_name = 'gulfstream-' + single_date.strftime("%m%d%Y") + '.pdf'
        try:
            with urllib.request.urlopen(url) as response, open(file_name, 'wb') as out_file:
                shutil.copyfileobj(response, out_file)
            print(url)
        except urllib.error.HTTPError as err:
            if err.code == 404:
                pass
            else:
                print(err)


def date_range(start_date, end_date):
    for n in range(int((end_date - start_date).days)):
        yield start_date + timedelta(n)


main()
