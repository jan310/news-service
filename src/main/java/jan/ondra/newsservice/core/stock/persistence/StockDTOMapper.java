package jan.ondra.newsservice.core.stock.persistence;

import jan.ondra.newsservice.core.stock.model.Stock;

class StockDTOMapper {

    static Stock getDomainModel(StockDTO stockDTO) {
        return new Stock(
            stockDTO.ticker(),
            stockDTO.companyName(),
            stockDTO.latestNewsLink()
        );
    }

}
