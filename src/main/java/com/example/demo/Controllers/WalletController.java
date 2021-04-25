package com.example.demo.Controllers;

import com.example.demo.DTOs.BuyTransactionDTO;
import com.example.demo.DTOs.WalletCoinDTO;
import com.example.demo.Entities.WalletCoin;
import com.example.demo.Mappers.BuyTransactionMapper;
import com.example.demo.Mappers.WalletCoinMapper;
import com.example.demo.Services.WalletService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Api
@RestController
@RequestMapping("/CoinTrader/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @PostMapping("/buy")
    public ResponseEntity buy(@RequestBody BuyTransactionDTO buyTransactionDTO) {
        walletService.addTransaction(buyTransactionDTO);
        return new ResponseEntity<>("Transaction added", HttpStatus.OK);
    }

    @GetMapping("/allTransactions")
    public List<BuyTransactionDTO> getAllTransactions() {
        return BuyTransactionMapper.mapTransactionListtoTransactionDTOList(walletService.getUserTransactions());
    }

    @GetMapping("/getWallet")
    public List<WalletCoinDTO> getWallet() throws IOException {
        return WalletCoinMapper.mapWalletCoinListtoWalletCoinDTOList(walletService.getWalletCoins());
    }


}