package jan.ondra.newsservice.core.newsarticle.persistence;

import jan.ondra.newsservice.core.newsarticle.model.CompanyNews;

class CompanyNewsDTOMapper {

    static CompanyNews getDomainModel(CompanyNewsDTO companyNewsDTO) {
        return new CompanyNews(
            companyNewsDTO.companyName(),
            companyNewsDTO.stockTicker(),
            companyNewsDTO.newsLink(),
            companyNewsDTO.newsSummary(),
            companyNewsDTO.newsSentiment()
        );
    }

}
