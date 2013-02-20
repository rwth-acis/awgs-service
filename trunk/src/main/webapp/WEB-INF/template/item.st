<!DOCTYPE html>
<html>
    <head>
        <title>ACIS Group Working Series - Item $item_id$</title>
		<meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <meta name="description" content="$item_description$">
		
		<link href="$root$/lib/bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen">
		
		<style type="text/css">
	
			#wrap {
				min-height: 100%;
				height: auto !important;
				height: 100%;
				margin: 0 auto -50px;
			}
			
			#footer {
				background-color: #f5f5f5;
			}

			@media (max-width: 767px) {
				#footer {
					margin-left: -20px;
					margin-right: -20px;
					padding-left: 20px;
					padding-right: 20px;
				}
			}

			.container-fluid {
				width: auto;
				max-width: 680px;
			}
			
			.container-fluid .credit {
				margin: 20px 0;
			}

		</style>
		
		<!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
		<!--[if lt IE 9]>
			<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
		<script type="text/javascript" src="$root$/lib/bootstrap/js/bootstrap.min.js"></script>
    </head>
    <body>
        
		<div id="wrap">
			<div id="content" class="container">
				
				<h2>$item_id$</h2>
				<h3>$item_title$</h3>
			
				<label for='owner' style='font-weight:bold;'>Owner:</label><div id='owner'><a href='xmpp:$item_owner$'>$item_owner$</a></div>
				<label for='description' style='font-weight:bold;'>Description:</label><div id='description'>$item_description$</div>
				<label for='url' style='font-weight:bold;'>URL:</label><div id='url'><a href='$item_url$'>$item_url$</a></div>
				<label for='status' style='font-weight:bold;'>Type:</label><div id='type'>$item_type$</div>
				<label for='lastup' style='font-weight:bold;'>Last Update:</label><div id='lastup'>$item_lastupdate$</div>
				
			</div>
			<div id="push"/>
		</div>
		
		<script type="text/javascript">
			var _gaq = _gaq || [];
			_gaq.push(['_setAccount', 'UA-38649683-1']);
			_gaq.push(['_trackPageview']);
			(function() {
				var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
				ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
				var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
			})();
		</script>
		
		
    </body>
</html>