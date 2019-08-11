package wf.bitcoin.javabitcoindrpcclient;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.Block;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.BlockWithTxInfo;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.ScanObject;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.UnspentTxOutput;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.UtxoSet;
import wf.bitcoin.krotjson.JSON;

/**
 * Created by fpeters on 11-01-17.
 */

public class BitcoinJSONRPCClientTest {

    class MyClientTest extends BitcoinJSONRPCClient {

        String expectedMethod;
        Object[] expectedObject;
        String result;

        MyClientTest(boolean testNet, String expectedMethod, Object[] expectedObject, String result) {
            super(testNet);
            this.expectedMethod = expectedMethod;
            this.expectedObject = expectedObject;
            this.result = result;
        }

        @Override
        public Object query(String method, Object... o) throws GenericRpcException {
            if(method!=expectedMethod) {
                throw new GenericRpcException("wrong method");
            }
            if(o.equals(expectedObject)){
                throw new GenericRpcException("wrong object");
            }
            return JSON.parse(result);
        }
    }

    MyClientTest client;

    @Test
    public void signRawTransactionTest() throws Exception {
        client = new MyClientTest(false, "signrawtransaction", null,
                                    "{\n" +
                                            "  \"hex\": \"0100000001b8b2244faca910c1ffff24ecd2b559b4699338398bf77e4cb1fdeb19ad419ea0010000006b483045022100b68b7fe9cfabb32949af6747b6769dffcf2aa4170e4df2f0e9d0a4571989e94e02204cf506c210cdb6b6b4413bf251a0b57ebcf1b1b2d303ba6183239b557ef0a310012102ab46e1d7b997d8094e97bc06a21a054c2ef485fac512e2dc91eb9831af55af4effffffff012e2600000000000017a9140b2d7ed4e5076383ba8e98b9b3bce426b7a2ea1e8700000000\",\n" +
                                            "  \"complete\": true\n" +
                                            "}\n");
        LinkedList<BitcoindRpcClient.ExtendedTxInput> inputList = new LinkedList<BitcoindRpcClient.ExtendedTxInput>();
        LinkedList<String> privateKeys = new LinkedList<String>();
        privateKeys.add("cSjzx3VAM1r9iLXLvL6N61oS3zKns9Z9DcocrbkEzesPTDHWm5r4");
        String hex = client.signRawTransaction("0100000001B8B2244FACA910C1FFFF24ECD2B559B4699338398BF77E4CB1FDEB19AD419EA0010000001976A9144CB4C3B90994FEF58FABB6D8368302E917C6EFB188ACFFFFFFFF012E2600000000000017A9140B2D7ED4E5076383BA8E98B9B3BCE426B7A2EA1E8700000000",
                                                inputList, privateKeys, "ALL");
        assertEquals("0100000001b8b2244faca910c1ffff24ecd2b559b4699338398bf77e4cb1fdeb19ad419ea0010000006b483045022100b68b7fe9cfabb32949af6747b6769dffcf2aa4170e4df2f0e9d0a4571989e94e02204cf506c210cdb6b6b4413bf251a0b57ebcf1b1b2d303ba6183239b557ef0a310012102ab46e1d7b997d8094e97bc06a21a054c2ef485fac512e2dc91eb9831af55af4effffffff012e2600000000000017a9140b2d7ed4e5076383ba8e98b9b3bce426b7a2ea1e8700000000",
                    hex);
    }

    @Test
    public void signRawTransactionTestException() throws Exception {
        client = new MyClientTest(false, "signrawtransaction", null,
                "{\n" +
                        "  \"hex\": \"0100000001b8b2244faca910c1ffff24ecd2b559b4699338398bf77e4cb1fdeb19ad419ea00100000000ffffffff012e2600000000000017a9140b2d7ed4e5076383ba8e98b9b3bce426b7a2ea1e8700000000\",\n" +
                        "  \"complete\": false,\n" +
                        "  \"errors\": [\n" +
                        "    {\n" +
                        "      \"txid\": \"a09e41ad19ebfdb14c7ef78b39389369b459b5d2ec24ffffc110a9ac4f24b2b8\",\n" +
                        "      \"vout\": 1,\n" +
                        "      \"scriptSig\": \"\",\n" +
                        "      \"sequence\": 4294967295,\n" +
                        "      \"error\": \"Operation not valid with the current stack size\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}");
        LinkedList<BitcoindRpcClient.ExtendedTxInput> inputList = new LinkedList<BitcoindRpcClient.ExtendedTxInput>();
        LinkedList<String> privateKeys = new LinkedList<String>();
        try {
            client.signRawTransaction("0100000001B8B2244FACA910C1FFFF24ECD2B559B4699338398BF77E4CB1FDEB19AD419EA0010000001976A9144CB4C3B90994FEF58FABB6D8368302E917C6EFB188ACFFFFFFFF012E2600000000000017A9140B2D7ED4E5076383BA8E98B9B3BCE426B7A2EA1E8700000000",
                    inputList, privateKeys, "ALL");
        }
        catch(Exception e) {
            assertThat(e.getMessage(), is("Incomplete"));
        }
    }

    @Test
    public void signRawTransactionTest2() throws Exception {
        client = new MyClientTest(false, "signrawtransaction", null,
                "{\n" +
                        "  \"hex\": \"0100000001b8b2244faca910c1ffff24ecd2b559b4699338398bf77e4cb1fdeb19ad419ea0010000006b483045022100b68b7fe9cfabb32949af6747b6769dffcf2aa4170e4df2f0e9d0a4571989e94e02204cf506c210cdb6b6b4413bf251a0b57ebcf1b1b2d303ba6183239b557ef0a310012102ab46e1d7b997d8094e97bc06a21a054c2ef485fac512e2dc91eb9831af55af4effffffff012e2600000000000017a9140b2d7ed4e5076383ba8e98b9b3bce426b7a2ea1e8700000000\",\n" +
                        "  \"complete\": true\n" +
                        "}\n");
        String hex = client.signRawTransaction("0100000001B8B2244FACA910C1FFFF24ECD2B559B4699338398BF77E4CB1FDEB19AD419EA0010000001976A9144CB4C3B90994FEF58FABB6D8368302E917C6EFB188ACFFFFFFFF012E2600000000000017A9140B2D7ED4E5076383BA8E98B9B3BCE426B7A2EA1E8700000000");
        assertEquals("0100000001b8b2244faca910c1ffff24ecd2b559b4699338398bf77e4cb1fdeb19ad419ea0010000006b483045022100b68b7fe9cfabb32949af6747b6769dffcf2aa4170e4df2f0e9d0a4571989e94e02204cf506c210cdb6b6b4413bf251a0b57ebcf1b1b2d303ba6183239b557ef0a310012102ab46e1d7b997d8094e97bc06a21a054c2ef485fac512e2dc91eb9831af55af4effffffff012e2600000000000017a9140b2d7ed4e5076383ba8e98b9b3bce426b7a2ea1e8700000000",
                    hex);
    }
    
    @Test
    public void getBlockTest() throws Exception {
      String blockId = "000000000000022512b381cf4e965e56b166e89e0295dffe0360c41063595d82";
      String json = "{\n" + 
          "  \"hash\": \"000000000000022512b381cf4e965e56b166e89e0295dffe0360c41063595d82\",\n" + 
          "  \"confirmations\": 192,\n" + 
          "  \"strippedsize\": 1212,\n" + 
          "  \"size\": 1466,\n" + 
          "  \"weight\": 5102,\n" + 
          "  \"height\": 1572537,\n" + 
          "  \"version\": 536870912,\n" + 
          "  \"versionHex\": \"20000000\",\n" + 
          "  \"merkleroot\": \"9da9aa3da6293f51c9e483755bb7c0bb84162d86db2b1b105b2a9301859c82c7\",\n" + 
          "  \"tx\": [\n" + 
          "    \"ca49cc95a945dbfd3cd5b7c2ae92a886f20f455fa61267ae07110efbd504d33f\",\n" + 
          "    \"21636dd94bc7aa1fd796facb9703cca0adcc4998e07825a0dde05c6d023e3f64\",\n" + 
          "    \"44c252f2e2de73e797c6f4f0a3220e27bea8e7ac7417258ab800acc3a90198f9\"\n" + 
          "  ],\n" + 
          "  \"time\": 1564928436,\n" + 
          "  \"mediantime\": 1564924700,\n" + 
          "  \"nonce\": 2890030326,\n" + 
          "  \"bits\": \"1a02a571\",\n" + 
          "  \"difficulty\": 6339886.697765605,\n" + 
          "  \"chainwork\": \"00000000000000000000000000000000000000000000012269df1fb3545ccac5\",\n" + 
          "  \"nTx\": 3,\n" + 
          "  \"previousblockhash\": \"00000000fc9f5ea7d0adce0c760ec6085914780b986567851916ff782f82bb11\",\n" + 
          "  \"nextblockhash\": \"00000000000615e73cc5b62ee9e5299aad4b95130e902a76d7c9219d09ea01bc\"\n" + 
          "}";
      client = new MyClientTest(false, "getblock", null, json);
      Block block = client.getBlock(blockId);
      assertEquals(blockId, block.hash());
      assertEquals(192, block.confirmations());
      assertEquals("9da9aa3da6293f51c9e483755bb7c0bb84162d86db2b1b105b2a9301859c82c7", block.merkleRoot());
      assertEquals(3, block.tx().size());
      assertEquals("ca49cc95a945dbfd3cd5b7c2ae92a886f20f455fa61267ae07110efbd504d33f", block.tx().get(0));
      assertEquals("21636dd94bc7aa1fd796facb9703cca0adcc4998e07825a0dde05c6d023e3f64", block.tx().get(1));
      assertEquals("44c252f2e2de73e797c6f4f0a3220e27bea8e7ac7417258ab800acc3a90198f9", block.tx().get(2));
    }
    
    @Test
    public void getBlockWithTxInfoTest() {
      String blockId = "00000000000000ec6fa98d9c870e1037891ef9c9d5b0e1e6861ffb79759096cb";
      String json = "{\n" + 
          "  \"hash\": \"00000000000000ec6fa98d9c870e1037891ef9c9d5b0e1e6861ffb79759096cb\",\n" + 
          "  \"confirmations\": 2,\n" + 
          "  \"strippedsize\": 383,\n" + 
          "  \"size\": 528,\n" + 
          "  \"weight\": 1677,\n" + 
          "  \"height\": 1573883,\n" + 
          "  \"version\": 536870912,\n" + 
          "  \"versionHex\": \"20000000\",\n" + 
          "  \"merkleroot\": \"03b2a3546bee744c434bcae2ad3c6b7349881d537e5cb4e7134acb4cee48d49d\",\n" + 
          "  \"tx\": [\n" + 
          "    {\n" + 
          "      \"txid\": \"abb74d53d5765c0ddc6061d3bdf50a81a943b5f971e98a2a993a02bb9c4f49f3\",\n" + 
          "      \"hash\": \"11f0e11ef7a1e0b0c5308aef8466ae5c820410ea0a3f18bd55e69cf935f8556b\",\n" + 
          "      \"version\": 2,\n" + 
          "      \"size\": 200,\n" + 
          "      \"vsize\": 173,\n" + 
          "      \"weight\": 692,\n" + 
          "      \"locktime\": 0,\n" + 
          "      \"vin\": [\n" + 
          "        {\n" + 
          "          \"coinbase\": \"03fb031804060c505d44434578706c6f726174696f6e09000feb9201000000000000\",\n" + 
          "          \"sequence\": 4294967295\n" + 
          "        }\n" + 
          "      ],\n" + 
          "      \"vout\": [\n" + 
          "        {\n" + 
          "          \"value\": 0.39062666,\n" + 
          "          \"n\": 0,\n" + 
          "          \"scriptPubKey\": {\n" + 
          "            \"asm\": \"OP_HASH160 e0725ef08b0046fd2df3c58d5fefc5580e1f59de OP_EQUAL\",\n" + 
          "            \"hex\": \"a914e0725ef08b0046fd2df3c58d5fefc5580e1f59de87\",\n" + 
          "            \"reqSigs\": 1,\n" + 
          "            \"type\": \"scripthash\",\n" + 
          "            \"addresses\": [\n" + 
          "              \"2NDhzMt2D9ZxXapbuq567WGeWP7NuDN81cg\"\n" + 
          "            ]\n" + 
          "          }\n" + 
          "        },\n" + 
          "        {\n" + 
          "          \"value\": 0.00000000,\n" + 
          "          \"n\": 1,\n" + 
          "          \"scriptPubKey\": {\n" + 
          "            \"asm\": \"OP_RETURN aa21a9ed5da890e9474351962ca8ada729556e3d6085493617f94f8789c57548de70096a\",\n" + 
          "            \"hex\": \"6a24aa21a9ed5da890e9474351962ca8ada729556e3d6085493617f94f8789c57548de70096a\",\n" + 
          "            \"type\": \"nulldata\"\n" + 
          "          }\n" + 
          "        }\n" + 
          "      ],\n" + 
          "      \"hex\": \"020000000001010000000000000000000000000000000000000000000000000000000000000000ffffffff2203fb031804060c505d44434578706c6f726174696f6e09000feb9201000000000000ffffffff028a0c54020000000017a914e0725ef08b0046fd2df3c58d5fefc5580e1f59de870000000000000000266a24aa21a9ed5da890e9474351962ca8ada729556e3d6085493617f94f8789c57548de70096a0120000000000000000000000000000000000000000000000000000000000000000000000000\"\n" + 
          "    },\n" + 
          "    {\n" + 
          "      \"txid\": \"0a2e6d705d249e2ee87e10022f6ab82cbb4b71557a767eb1d20e36b7d4995e63\",\n" + 
          "      \"hash\": \"01074ea19bfa2edf8602c1ebebd29c461be25ad034b3dc27c999d9b51d5baad8\",\n" + 
          "      \"version\": 2,\n" + 
          "      \"size\": 247,\n" + 
          "      \"vsize\": 166,\n" + 
          "      \"weight\": 661,\n" + 
          "      \"locktime\": 1573881,\n" + 
          "      \"vin\": [\n" + 
          "        {\n" + 
          "          \"txid\": \"5868cb805dcd5e30b1321d952bdbe976247cc482836d223c49241732df4bb32e\",\n" + 
          "          \"vout\": 1,\n" + 
          "          \"scriptSig\": {\n" + 
          "            \"asm\": \"001473221217cb61944d7f59b6673e99a6d1e0525543\",\n" + 
          "            \"hex\": \"16001473221217cb61944d7f59b6673e99a6d1e0525543\"\n" + 
          "          },\n" + 
          "          \"txinwitness\": [\n" + 
          "            \"304402203bb502491acc3b083c0b82460adede727b6d5395a78001077cd8244719d1a4d10220306f99c7a25b7e2a1719093b569db179300345d26b9a6ef98391f5bd8e71d98301\",\n" + 
          "            \"032429001d8a433e8b93cc74bd6d0c3889a4c72d650abc587dee059e492608741a\"\n" + 
          "          ],\n" + 
          "          \"sequence\": 4294967294\n" + 
          "        }\n" + 
          "      ],\n" + 
          "      \"vout\": [\n" + 
          "        {\n" + 
          "          \"value\": 0.00100000,\n" + 
          "          \"n\": 0,\n" + 
          "          \"scriptPubKey\": {\n" + 
          "            \"asm\": \"OP_HASH160 c6953538c43fb99ef5f53351e3d5490357986df7 OP_EQUAL\",\n" + 
          "            \"hex\": \"a914c6953538c43fb99ef5f53351e3d5490357986df787\",\n" + 
          "            \"reqSigs\": 1,\n" + 
          "            \"type\": \"scripthash\",\n" + 
          "            \"addresses\": [\n" + 
          "              \"2NBMEXQu3GJK7ohHUQovDZQedUiB3tVP64t\"\n" + 
          "            ]\n" + 
          "          }\n" + 
          "        },\n" + 
          "        {\n" + 
          "          \"value\": 504.53822796,\n" + 
          "          \"n\": 1,\n" + 
          "          \"scriptPubKey\": {\n" + 
          "            \"asm\": \"OP_HASH160 7b5cbdb8e20d9abf1870bda6a5267a2e3f264ec2 OP_EQUAL\",\n" + 
          "            \"hex\": \"a9147b5cbdb8e20d9abf1870bda6a5267a2e3f264ec287\",\n" + 
          "            \"reqSigs\": 1,\n" + 
          "            \"type\": \"scripthash\",\n" + 
          "            \"addresses\": [\n" + 
          "              \"2N4VWCeJAnYwEnEaAEueXNm9x9T7VLP1toJ\"\n" + 
          "            ]\n" + 
          "          }\n" + 
          "        }\n" + 
          "      ],\n" + 
          "      \"hex\": \"020000000001012eb34bdf321724493c226d8382c47c2476e9db2b951d32b1305ecd5d80cb6858010000001716001473221217cb61944d7f59b6673e99a6d1e0525543feffffff02a08601000000000017a914c6953538c43fb99ef5f53351e3d5490357986df7874c3d48bf0b00000017a9147b5cbdb8e20d9abf1870bda6a5267a2e3f264ec2870247304402203bb502491acc3b083c0b82460adede727b6d5395a78001077cd8244719d1a4d10220306f99c7a25b7e2a1719093b569db179300345d26b9a6ef98391f5bd8e71d9830121032429001d8a433e8b93cc74bd6d0c3889a4c72d650abc587dee059e492608741af9031800\"\n" + 
          "    }\n" + 
          "  ],\n" + 
          "  \"time\": 1565527058,\n" + 
          "  \"mediantime\": 1565524421,\n" + 
          "  \"nonce\": 410686640,\n" + 
          "  \"bits\": \"1a02a571\",\n" + 
          "  \"difficulty\": 6339886.697765605,\n" + 
          "  \"chainwork\": \"0000000000000000000000000000000000000000000001242b904912d50c9e99\",\n" + 
          "  \"nTx\": 2,\n" + 
          "  \"previousblockhash\": \"000000000000020f1516e84c5a278fa10a262929c6f483cc92adf25ba25e1def\",\n" + 
          "  \"nextblockhash\": \"00000000000000cce3a1921fca63353d13142f5d4bb43b431414f135acccc26c\"\n" + 
          "}\n" + 
          "";
      
      client = new MyClientTest(false, "getblock", new Object[] { 2 }, json);
      BlockWithTxInfo block = client.getBlockWithTxInfo(blockId);
      
      assertEquals(2, block.confirmations());
      assertEquals("03b2a3546bee744c434bcae2ad3c6b7349881d537e5cb4e7134acb4cee48d49d", block.merkleRoot());
      assertEquals(2, block.tx().size());
      assertEquals("1a02a571", block.bits());

      
      assertEquals("abb74d53d5765c0ddc6061d3bdf50a81a943b5f971e98a2a993a02bb9c4f49f3", block.tx().get(0).txId());
      assertEquals(1, block.tx().get(0).vIn().size());
      assertEquals(2, block.tx().get(0).vOut().size());
      assertEquals("2NDhzMt2D9ZxXapbuq567WGeWP7NuDN81cg", block.tx().get(0).vOut().get(0).scriptPubKey().addresses().get(0));
    }
    
    @Test
    public void scanTxOutSetTest() {
      ScanObject scanObject1 = new ScanObject("addr(mtoffFXQWh6YNP86TRsRETNn9nDaMmsKsL)", null);
      ScanObject scanObject2 = new ScanObject("addr(mi11rWuB14Eb2L5tpdqfD77DGMhschQdgx)", null);
      List<ScanObject> list = Arrays.asList(scanObject1, scanObject2);
      
      String json = "{\n" + 
          "  \"success\": true,\n" + 
          "  \"searched_items\": 22462153,\n" + 
          "  \"unspents\": [\n" + 
          "    {\n" + 
          "      \"txid\": \"6415d590f46344a6f72c0e1544eb183a5ac3d8ff9a2ab48435f3255794af3915\",\n" + 
          "      \"vout\": 0,\n" + 
          "      \"scriptPubKey\": \"76a9141b3edeb7188b1cef9996e81ae22b68dfb3f7806688ac\",\n" + 
          "      \"amount\": 0.00900000,\n" + 
          "      \"height\": 1442023\n" + 
          "    },\n" + 
          "    {\n" + 
          "      \"txid\": \"2d3bb59ba7bf690b43f604d7289e76534a9a32e92dd4f1945413a59832fe0723\",\n" + 
          "      \"vout\": 0,\n" + 
          "      \"scriptPubKey\": \"76a91491c2d21b865e338794bc92326de5dd0c15663d8788ac\",\n" + 
          "      \"amount\": 0.00300000,\n" + 
          "      \"height\": 1441179\n" + 
          "    },\n" + 
          "    {\n" + 
          "      \"txid\": \"b6573ad024dd97172238712a8d417e39ff9fbeb15e35bbae447b86966503289b\",\n" + 
          "      \"vout\": 1,\n" + 
          "      \"scriptPubKey\": \"76a91491c2d21b865e338794bc92326de5dd0c15663d8788ac\",\n" + 
          "      \"amount\": 0.00200000,\n" + 
          "      \"height\": 1440923\n" + 
          "    }\n" + 
          "  ],\n" + 
          "  \"total_amount\": 0.01400000\n" + 
          "}\n" + 
          "";
      
      client = new MyClientTest(false, "scantxoutset", new Object[] { "start", list }, json);
      UtxoSet utxoSet = client.scanTxOutSet(list);
      assertEquals(22462153, utxoSet.searchedItems().intValue());
      assertEquals(new BigDecimal("0.01400000"), utxoSet.totalAmount());
      assertEquals(3, utxoSet.unspents().size());
      UnspentTxOutput utxo = utxoSet.unspents().get(0);
      assertEquals("6415d590f46344a6f72c0e1544eb183a5ac3d8ff9a2ab48435f3255794af3915", utxo.txid());
      assertEquals(0, utxo.vout().intValue());
    }
    
}