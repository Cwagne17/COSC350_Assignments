import scrapy, json
import pandas as pd
import tabulate

class Faculty_Scrap(scrapy.Spider):
  name="faculty"

  def start_requests(self):
    url="https://www.towson.edu/fcsm/departments/computerinfosci/facultystaff/"
    yield scrapy.Request(url=url, callback=self.parse)
  
  def parse(self, response, **kwargs):
    with open('body.html', 'w') as file:
      file.write(response.text)
    
    with open(f'{response.url.split("/")[-2]}.json', 'w') as file:
      faculty_dict = {}
      for i in range(1, len(response.xpath('//*[@class="wysiwyg"]/table'))+1):
        faculty_dict[f'table{i}'] = []
        headers = [h.extract().replace(u'\xa0',u'') for h in response.xpath(f'//*[@class="wysiwyg"]/table[{i}]//th//text()')]
        
        for j in range(1, len(response.xpath(f'//*[@class="wysiwyg"]/table[{i}]//tr'))+1):
          faculty = {}
          for k in range(1, len(headers)+1):
            text = (response.xpath(f'normalize-space(//*[@class="wysiwyg"]/table[{i}]//tr[{j}]/td[{k}])').extract()[0]).replace(u'\xa0', u'')
            if text != '':
              faculty[headers[k-1]] = text
          if len(faculty.keys()) != 0:
            faculty_dict[f'table{i}'].append(faculty)
            
      file.write(json.dumps(faculty_dict, indent=2))
      
      for table in faculty_dict:
        data = [obj.values() for obj in faculty_dict[table]]
        df = pd.DataFrame(data, columns=faculty_dict[table][0].keys())
        print(df)

