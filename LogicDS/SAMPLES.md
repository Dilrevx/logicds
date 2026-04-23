# LogicDS — Projects & CVEs

Companion reference for [`LogicDS/README.md`](README.md). Contains the project-to-sample inventory and the sample-to-CVE mapping for the 61 real-world samples.

## Projects Considered

The 61 real-world samples are drawn from **28 distinct upstream projects**. Samples that share a project also share the buggy / fixed source tree at different commits; the list below shows every real sample alongside its project, sorted by contribution size.

| Project | Real Samples | #Vulnerabilities |
|---|---|---:|
| [OpenSSL](https://github.com/openssl/openssl) | sample_1, sample_2, sample_3, sample_4, sample_5, sample_6, sample_7, sample_14, sample_16, sample_31, sample_32 | 11 |
| [srsRAN](https://github.com/srsran) | sample_36, sample_37, sample_38, sample_39, sample_40, sample_41, sample_42, sample_43 | 8 |
| [wolfSSL](https://github.com/wolfSSL/wolfssl) | sample_12, sample_21, sample_22, sample_23, sample_24, sample_25 | 6 |
| [BIND](https://gitlab.isc.org/isc-projects/bind9) | sample_13, sample_26, sample_27, sample_28 | 4 |
| [GnuTLS](https://gitlab.com/gnutls/gnutls) | sample_9, sample_18, sample_19 | 3 |
| [Mbed TLS](https://github.com/Mbed-TLS/mbedtls) | sample_10, sample_11, sample_20 | 3 |
| [Answer](https://github.com/answerdev/answer) | sample_58, sample_59 | 2 |
| [Dnsmasq](https://github.com/imp/dnsmasq) | sample_29, sample_30 | 2 |
| [Krb5](https://github.com/krb5/krb5) | sample_46, sample_47 | 2 |
| [Mosquitto](https://github.com/eclipse-mosquitto/mosquitto) | sample_33, sample_45 | 2 |
| [Radicale](https://github.com/Unrud/Radicale) | sample_57 | 1 |
| [Atheme](https://github.com/atheme/atheme) | sample_61 | 1 |
| [Authelia](https://github.com/authelia/authelia) | sample_50 | 1 |
| [Calibre-Web](https://github.com/janeczku/calibre-web) | sample_56 | 1 |
| [Dropbear](https://github.com/mkj/dropbear) | sample_17 | 1 |
| [Exporter-Toolkit](https://github.com/prometheus/exporter-toolkit) | sample_60 | 1 |
| [Gitea](https://github.com/go-gitea/gitea) | sample_49 | 1 |
| [Hostap](https://w1.fi/hostap.git) | sample_15 | 1 |
| [libSSH](https://git.libssh.org/projects/libssh.git) | sample_8 | 1 |
| [Memos](https://github.com/usememos/memos) | sample_48 | 1 |
| [Monkey](https://github.com/monkey/monkey) | sample_53 | 1 |
| [Netmaker](https://github.com/gravitl/netmaker) | sample_55 | 1 |
| [Openfire](https://github.com/igniterealtime/Openfire) | sample_35 | 1 |
| [OpenSSH-Portable](https://github.com/openssh/openssh-portable) | sample_44 | 1 |
| [PJProject](https://github.com/pjsip/pjproject) | sample_34 | 1 |
| [QEMU](https://github.com/qemu/qemu) | sample_54 | 1 |
| [SPNEGO-HTTP-Auth-Nginx-Module](https://github.com/stnoonan/spnego-http-auth-nginx-module) | sample_51 | 1 |
| [Cherokee Webserver](https://github.com/cherokee/webserver) | sample_52 | 1 |
| **Total** | 61 | **61** |

## CVE Mapping

Of the 61 real-world samples, **53 are backed by a publicly tracked CVE**. The remaining 8 samples (`sample_36`–`sample_43`) are academically disclosed srsRAN NAS logical vulnerabilities without assigned CVE identifiers; each sample's `vulnerability_description.txt` describes the flaw inline.

| Sample | CVE |
|---|---|
| sample_1 | [CVE-2023-5363](https://nvd.nist.gov/vuln/detail/CVE-2023-5363) |
| sample_2 | [CVE-2023-0465](https://nvd.nist.gov/vuln/detail/CVE-2023-0465) |
| sample_3 | [CVE-2022-3358](https://nvd.nist.gov/vuln/detail/CVE-2022-3358) |
| sample_4 | [CVE-2022-1434](https://nvd.nist.gov/vuln/detail/CVE-2022-1434) |
| sample_5 | [CVE-2022-1343](https://nvd.nist.gov/vuln/detail/CVE-2022-1343) |
| sample_6 | [CVE-2019-1543](https://nvd.nist.gov/vuln/detail/CVE-2019-1543) |
| sample_7 | [CVE-2015-1793](https://nvd.nist.gov/vuln/detail/CVE-2015-1793) |
| sample_8 | [CVE-2023-48795](https://nvd.nist.gov/vuln/detail/CVE-2023-48795) |
| sample_9 | [CVE-2020-11501](https://nvd.nist.gov/vuln/detail/CVE-2020-11501) |
| sample_10 | [CVE-2020-36478](https://nvd.nist.gov/vuln/detail/CVE-2020-36478) |
| sample_11 | [CVE-2020-36425](https://nvd.nist.gov/vuln/detail/CVE-2020-36425) |
| sample_12 | [CVE-2022-25640](https://nvd.nist.gov/vuln/detail/CVE-2022-25640) |
| sample_13 | [CVE-2019-6465](https://nvd.nist.gov/vuln/detail/CVE-2019-6465) |
| sample_14 | [CVE-2014-0224](https://nvd.nist.gov/vuln/detail/CVE-2014-0224) |
| sample_15 | [CVE-2023-52160](https://nvd.nist.gov/vuln/detail/CVE-2023-52160) |
| sample_16 | [CVE-2015-0205](https://nvd.nist.gov/vuln/detail/CVE-2015-0205) |
| sample_17 | [CVE-2021-36369](https://nvd.nist.gov/vuln/detail/CVE-2021-36369) |
| sample_18 | [CVE-2016-7444](https://nvd.nist.gov/vuln/detail/CVE-2016-7444) |
| sample_19 | [CVE-2015-0294](https://nvd.nist.gov/vuln/detail/CVE-2015-0294) |
| sample_20 | [CVE-2020-36477](https://nvd.nist.gov/vuln/detail/CVE-2020-36477) |
| sample_21 | [CVE-2023-3724](https://nvd.nist.gov/vuln/detail/CVE-2023-3724) |
| sample_22 | [CVE-2022-25638](https://nvd.nist.gov/vuln/detail/CVE-2022-25638) |
| sample_23 | [CVE-2022-23408](https://nvd.nist.gov/vuln/detail/CVE-2022-23408) |
| sample_24 | [CVE-2021-38597](https://nvd.nist.gov/vuln/detail/CVE-2021-38597) |
| sample_25 | [CVE-2021-3336](https://nvd.nist.gov/vuln/detail/CVE-2021-3336) |
| sample_26 | [CVE-2017-3142](https://nvd.nist.gov/vuln/detail/CVE-2017-3142) |
| sample_27 | [CVE-2022-0396](https://nvd.nist.gov/vuln/detail/CVE-2022-0396) |
| sample_28 | [CVE-2019-6475](https://nvd.nist.gov/vuln/detail/CVE-2019-6475) |
| sample_29 | [CVE-2020-25684](https://nvd.nist.gov/vuln/detail/CVE-2020-25684) |
| sample_30 | [CVE-2019-19270](https://nvd.nist.gov/vuln/detail/CVE-2019-19270) |
| sample_31 | [CVE-2021-4044](https://nvd.nist.gov/vuln/detail/CVE-2021-4044) |
| sample_32 | [CVE-2021-3450](https://nvd.nist.gov/vuln/detail/CVE-2021-3450) |
| sample_33 | [CVE-2018-12550](https://nvd.nist.gov/vuln/detail/CVE-2018-12550) |
| sample_34 | [CVE-2022-39269](https://nvd.nist.gov/vuln/detail/CVE-2022-39269) |
| sample_35 | [CVE-2024-25420](https://nvd.nist.gov/vuln/detail/CVE-2024-25420) |
| sample_44 | [CVE-2018-20685](https://nvd.nist.gov/vuln/detail/CVE-2018-20685) |
| sample_45 | [CVE-2018-20145](https://nvd.nist.gov/vuln/detail/CVE-2018-20145) |
| sample_46 | [CVE-2014-9422](https://nvd.nist.gov/vuln/detail/CVE-2014-9422) |
| sample_47 | [CVE-2015-2694](https://nvd.nist.gov/vuln/detail/CVE-2015-2694) |
| sample_48 | [CVE-2023-4696](https://nvd.nist.gov/vuln/detail/CVE-2023-4696) |
| sample_49 | [CVE-2022-0905](https://nvd.nist.gov/vuln/detail/CVE-2022-0905) |
| sample_50 | [CVE-2021-32637](https://nvd.nist.gov/vuln/detail/CVE-2021-32637) |
| sample_51 | [CVE-2021-21335](https://nvd.nist.gov/vuln/detail/CVE-2021-21335) |
| sample_52 | [CVE-2014-4668](https://nvd.nist.gov/vuln/detail/CVE-2014-4668) |
| sample_53 | [CVE-2013-2182](https://nvd.nist.gov/vuln/detail/CVE-2013-2182) |
| sample_54 | [CVE-2020-35517](https://nvd.nist.gov/vuln/detail/CVE-2020-35517) |
| sample_55 | [CVE-2023-32077](https://nvd.nist.gov/vuln/detail/CVE-2023-32077) |
| sample_56 | [CVE-2022-0273](https://nvd.nist.gov/vuln/detail/CVE-2022-0273) |
| sample_57 | [CVE-2015-8748](https://nvd.nist.gov/vuln/detail/CVE-2015-8748) |
| sample_58 | [CVE-2023-4815](https://nvd.nist.gov/vuln/detail/CVE-2023-4815) |
| sample_59 | [CVE-2023-1537](https://nvd.nist.gov/vuln/detail/CVE-2023-1537) |
| sample_60 | [CVE-2022-46146](https://nvd.nist.gov/vuln/detail/CVE-2022-46146) |
| sample_61 | [CVE-2022-24976](https://nvd.nist.gov/vuln/detail/CVE-2022-24976) |
